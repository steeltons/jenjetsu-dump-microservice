package org.jenjetsu.com.core.service.implementation;

import jakarta.transaction.Transactional;
import org.jenjetsu.com.core.entity.RefreshToken;
import org.jenjetsu.com.core.repository.RefreshTokenRepository;
import org.jenjetsu.com.core.service.RefreshTokenService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Primary
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository tokenRep;

    public RefreshTokenServiceImpl(RefreshTokenRepository tokenRep) {
        this.tokenRep = tokenRep;
    }

    @Override
    public void create(RefreshToken refreshToken) {
        if(existById(refreshToken.getId())) {
            throw new IllegalArgumentException("RefreshToken is already exist");
        }
        tokenRep.save(refreshToken);
    }

    @Override
    public void createAll(Collection<RefreshToken> refreshTokens) {
        tokenRep.saveAll(refreshTokens);
    }

    @Override
    public boolean delete(RefreshToken refreshToken) {
        return deleteById(refreshToken.getId());
    }

    @Override
    public boolean deleteById(String id) {
        boolean res = false;
        if(existById(id)) {
            tokenRep.deleteById(id);
            res = true;
        }
        return res;
    }

    @Override
    public boolean existById(String id) {
        return id != null && tokenRep.existsById(id);
    }

    @Override
    @Transactional
    public void deleteByOwnerId(String ownerId) {
        tokenRep.deleteAllByOwnerId(ownerId);
    }
}
