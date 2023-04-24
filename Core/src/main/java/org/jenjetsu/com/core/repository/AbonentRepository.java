package org.jenjetsu.com.core.repository;

import org.jenjetsu.com.core.entity.Abonent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbonentRepository extends JpaRepository<Abonent, Long> {

    public void deleteByBalanceLessThan(double lessSum);
    public Optional<Abonent> findByPhoneNumber(Long PhoneNumber);
    public boolean existsByPhoneNumber(Long phoneNumber);
}
