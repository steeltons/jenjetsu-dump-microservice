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
@Entity(name = "tariff")
public class Tariff {
    @Id
    @Column(name = "id", length = 2)
    private String id; // Плохое решение для ключа, но не хватило времени для переписывания во всех модулях
    @Column(name = "basic_price")
    @Min(0)
    private double basicPrice;
    @Min(0)
    private double inputCost;
    @Min(0)
    private double outputCost;
    @OneToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "tariff")
    private TariffOption options;
    @Column(name = "monetary_unit", nullable = false, length = 12)
    private String monetaryUnit;

}
