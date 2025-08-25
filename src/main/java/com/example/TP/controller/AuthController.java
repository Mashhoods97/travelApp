package com.example.TP.controller;

import com.example.TP.enums.ResponseEnum;
import com.example.TP.model.Business;
import com.example.TP.model.User;
import com.example.TP.payload.request.LoginRequest;
import com.example.TP.payload.response.JwtResponse;
import com.example.TP.payload.response.ResponseModel;
import com.example.TP.repository.BusinessRepo;
import com.example.TP.security.jwt.JwtBlacklistService;
import com.example.TP.security.jwt.JwtUtils;
import com.example.TP.security.services.UserDetailsImpl;
import com.example.TP.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtBlacklistService jwtBlacklistService;
    @Autowired
    UserService userService;
    @Autowired
    BusinessRepo businessRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseModel<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userService.findOptionalByUsernameAndActive(loginRequest.getUsername());
        if (optionalUser.isPresent()) {
            try {
                if (optionalUser.get().getRole() == null) {
                    return new ResponseModel<>().failed(ResponseEnum.BAD_REQUEST.getStatus(), "This user has not been assigned a role.", null);
                }
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> privileges = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

//                Business business = businessRepo.findByIdAndArchive(userDetails.getBusinessId(), false);
//                BusinessMobileResponse businessResponse = modelMapper.map(business, BusinessMobileResponse.class);

                Optional<User> user = userService.findByUsername(loginRequest.getUsername());

                int type = user.map(User::getType).orElse(0);
                //logger.error("Cannot set user authentication: {}", e);
                return new ResponseModel<>().success(ResponseEnum.OK.getStatus(), new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        type,
                        privileges));
            }catch (BadCredentialsException ex){
                return new ResponseModel<>().failed(ResponseEnum.BAD_CREDENTIALS.getStatus(),
                        "Invalid credentials!", null);
            }
        }
        return new ResponseModel<>().failed(ResponseEnum.NOT_FOUND.getStatus(),
                "Error: User not found or not active!", null);
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
//        if(userRepo.existsByUsername(signUpRequest.getUsername())) {
//            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"),
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        if(userRepo.existsByEmail(signUpRequest.getEmail())) {
//            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"),
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        User user = new User();
//        user.setUsername(signUpRequest.getUsername());
//        user.setEmail(signUpRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
//        user.setFirstName(signUpRequest.getFirstName());
//        user.setLastName(signUpRequest.getLastName());
//        user.setRoleId(2); // Default role: USER
//        user.setActive(true);
//
//        User result = userRepo.save(user);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentContextPath().path("/users/{username}")
//                .buildAndExpand(result.getUsername()).toUri();
//
//        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
//    }

    @ExceptionHandler(Exception.class)
    public ResponseModel<?> handleException(Exception e) {
        log.error("Exception occurred: {}", e.getMessage());
        if (e.getClass().getSimpleName().equalsIgnoreCase("AccessDeniedException")) {
            return new ResponseModel<>().failed(ResponseEnum.FORBIDDEN.getStatus(), "AccessDenied", null);
        } else {
            return new ResponseModel<>().failed(ResponseEnum.INTERNAL_SERVER_ERROR.getStatus(), e.getMessage(), null);
        }
    }
}
