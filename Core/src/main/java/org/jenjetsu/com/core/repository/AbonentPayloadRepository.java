package org.jenjetsu.com.core.repository;

import org.jenjetsu.com.core.entity.AbonentPayload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AbonentPayloadRepository extends JpaRepository<AbonentPayload, Long> {

    public List<AbonentPayload> findAbonentPayloadByAbonentPhoneNumber(Long phoneNumber);
}
