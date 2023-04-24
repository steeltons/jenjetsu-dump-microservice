package org.jenjetsu.com.core.service;

import org.jenjetsu.com.core.entity.RefreshToken;

public interface RefreshTokenService extends CreateDao<RefreshToken, String>,
                                             DeleteDao<RefreshToken, String>{

    public boolean existById(String id);
    public void deleteByOwnerId(String ownerId);
}
