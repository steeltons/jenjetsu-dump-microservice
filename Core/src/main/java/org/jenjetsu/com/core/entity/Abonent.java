package org.jenjetsu.com.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "abonent")
public class Abonent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "phone_number", unique = true)
    @Min(70000000000l) @Max(89999999999l)
    private Long phoneNumber;
    @Column(name = "balance", nullable = false)
    @Min(-1000) @Max(10000)
    private double balance;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "abonent")
    private List<AbonentPayload> payloadList;
    public void addMoney(double money) {
        this.balance += money;
    }

    public void subMoney(double money) {
        this.balance -= money;
    }

}
