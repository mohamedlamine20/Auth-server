package com.personel.auth.server.services;

import com.personel.auth.server.exceptions.InvalidCredentialsException;
import com.personel.auth.server.exceptions.ResourceAlreadyExistsException;
import com.personel.auth.server.exceptions.ResourceNotFoundException;
import com.personel.auth.server.modeles.ERole;
import com.personel.auth.server.modeles.Role;
import com.personel.auth.server.modeles.User;
import com.personel.auth.server.payload.request.LoginRequest;
import com.personel.auth.server.payload.request.SignupRequest;
import com.personel.auth.server.payload.response.JwtResponse;
import com.personel.auth.server.repository.RoleRepository;
import com.personel.auth.server.repository.UserRepository;
import com.personel.auth.server.security.jwt.JwtUtils;
import com.personel.auth.server.security.services.UserDetailsImpl;
import com.personel.auth.server.validators.ObjectsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
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
    private final ObjectsValidator validator;

    public AuthService(ObjectsValidator validator) {
        this.validator = validator;
    }

    public void registerUser(SignupRequest signupRequest){

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
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
    }
    public JwtResponse authenticateUser(LoginRequest loginRequest){
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

            return new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);

        }catch (Exception e){
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }
    public User getLoggedInUser(Principal principal){
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User is not found"));
        return user;
    }
}
