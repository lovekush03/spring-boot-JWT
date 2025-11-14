package com.example.jwt.Service;

import com.example.jwt.CustomExceptions.RefreshTokenExpiredException;
import com.example.jwt.Entity.RefreshToken;
import com.example.jwt.Entity.User;
import com.example.jwt.Repository.RefreshTokenRepository;
import com.example.jwt.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value(value = "${jwt.refreshToken.expiry}")
    private long refreshTokenExpiry;

    public RefreshToken createRefreshToken(User user){
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .createdAt(Instant.now())
                .lastUsedAt(Instant.now())
                .expiryDate(Instant.now().plusSeconds(refreshTokenExpiry))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiry(RefreshToken token){
        if(token.getExpiryDate().isBefore(Instant.now())){
            token.setRevoked(true);
            refreshTokenRepository.save(token);
            throw new RefreshTokenExpiredException("Refresh Token Expired. Please Make a new login Request");
        }
        return token;
    }

    @Transactional
    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public int revokeAllTokensForUser(User user) {
        //mark all tokens for a user revoked
        var tokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
        tokens.forEach(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
        });
        return tokens.size();
    }

    @Transactional
    public void updateLastUsed(RefreshToken token) {
        token.setLastUsedAt(Instant.now());
        refreshTokenRepository.save(token);
    }
}
