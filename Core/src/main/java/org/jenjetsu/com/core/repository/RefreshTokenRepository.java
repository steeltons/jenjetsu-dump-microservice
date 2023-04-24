package org.jenjetsu.com.core.repository;

import org.jenjetsu.com.core.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    public void deleteAllByOwnerId(String ownerId);
}
