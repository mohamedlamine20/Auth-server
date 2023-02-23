package com.personel.auth.server.controller;

import com.personel.auth.server.exceptions.InvalidCredentialsException;
import com.personel.auth.server.exceptions.ObjectNotValidException;
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
import com.personel.auth.server.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(Principal principal) {
        User user = authService.getLoggedInUser(principal);
        return ResponseEntity.ok(new CurrentUserResponse(user.getId(),user.getUsername(),user.getEmail()));
    }
    @GetMapping("/logout")
    public String signOut() {
        return "Logout route";
    }

}
