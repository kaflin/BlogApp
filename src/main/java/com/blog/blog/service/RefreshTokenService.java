package com.blog.blog.service;
import com.blog.blog.Model.RefreshToken;
import com.blog.blog.exceptions.SpringRedditException;
import com.blog.blog.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken= new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);

    }

    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(()->new SpringRedditException("Invalid refresh Token"));
    }
    public void deleteRefreshToken(String token)
    {
        refreshTokenRepository.deleteByToken(token);
    }
}
