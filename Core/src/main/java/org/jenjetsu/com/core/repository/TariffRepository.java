package org.jenjetsu.com.core.repository;

import org.jenjetsu.com.core.entity.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, String> {
}
