package com.personel.auth.server.controller;

import com.personel.auth.server.exceptions.InvalidCredentialsException;
import com.personel.auth.server.exceptions.ResourceNotFoundException;
import com.personel.auth.server.modeles.ERole;
import com.personel.auth.server.modeles.Role;
import com.personel.auth.server.modeles.User;
import com.personel.auth.server.payload.request.LoginRequest;
import com.personel.auth.server.payload.request.SignupRequest;
import com.personel.auth.server.payload.response.CurrentUserResponse;
import com.personel.auth.server.payload.response.JwtResponse;
import com.personel.auth.server.payload.response.MessageResponse;
import com.personel.auth.server.repository.RoleRepository;
import com.personel.auth.server.repository.UserRepository;
import com.personel.auth.server.security.jwt.JwtUtils;
import com.personel.auth.server.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            Map<String, Object> extraClaims = new HashMap<String, Object>(){
                {
                    put("id",userDetails.getId());
                    put("username",userDetails.getUsername());
                    put("email",userDetails.getEmail());
                    put("roles",roles);            }
            };

            String jwt = jwtUtils.generateJwtToken(extraClaims,userDetails);

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));

        }catch (Exception e){
            System.out.println(e);
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role is not found"));
            roles.add(userRole);
        } else {
            for (String role : strRoles) {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new ResourceNotFoundException("Role is not found"));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new ResourceNotFoundException("Role is not found"));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new ResourceNotFoundException("Role is not found"));
                        roles.add(userRole);
                }
            }
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @GetMapping("/logout")
    public String signOut() {
        return "Logout route";
    }
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User is not found"));
        return ResponseEntity.ok(new CurrentUserResponse(user.getId(),user.getUsername(),user.getEmail()));
    }
}
