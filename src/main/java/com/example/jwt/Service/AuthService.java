package com.example.jwt.Service;

import com.example.jwt.CustomExceptions.RefreshTokenNotFoundException;
import com.example.jwt.CustomExceptions.UserAlreadyExistsException;
import com.example.jwt.CustomExceptions.UserNotFoundException;
import com.example.jwt.Dto.AuthResponseDTO;
import com.example.jwt.Dto.RefreshTokenRequestDTO;
import com.example.jwt.Entity.RefreshToken;
import com.example.jwt.Entity.User;
import com.example.jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public String registerUser(User user){
        Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if(userOptional.isPresent()){
            throw new UserAlreadyExistsException("User with given username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User Created Successfully";
    }

    public ResponseEntity<AuthResponseDTO> authenticateUser(User user){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        User dbUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        String accessToken = jwtService.generateToken(dbUser.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(dbUser);
        return new ResponseEntity<>(
                AuthResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .build(),
                HttpStatus.OK
        );
    }

//    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(RefreshTokenRequestDTO refreshTokenRequest){
        String refreshToken = refreshTokenRequest.getRefreshToken();
        return refreshTokenService.findByToken(refreshToken)
                .map(token -> refreshTokenService.verifyExpiry(token))
                .map(token -> {
                   refreshTokenService.updateLastUsed(token);
                   String newAccessToken = jwtService.generateToken(token.getUser().getUsername());
                   return new ResponseEntity<>(
                           AuthResponseDTO.builder()
                                   .accessToken(newAccessToken)
                                   .refreshToken(token.getToken())
                                   .build(),
                           HttpStatus.OK
                   );
                })
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh Token Not Found !"));
    }

//    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDTO refreshTokenRequest){
        String refreshToken = refreshTokenRequest.getRefreshToken();
        refreshTokenService.findByToken(refreshToken).ifPresent(refreshTokenService::revokeToken);
        return new ResponseEntity<>(
                "Logged out Successfully(Refresh Token Revoked)",
                HttpStatus.OK
        );
    }
}
