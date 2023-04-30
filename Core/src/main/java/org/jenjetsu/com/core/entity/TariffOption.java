package org.jenjetsu.com.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tariff_option")
public class TariffOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "tariff_id", nullable = false, unique = false)
    private Tariff tariff;
    @Column(name = "incoming_buffer_cost", nullable = false)
    private double incomingBufferCost;
    @Column(name = "outcoming_buffer_cost", nullable = false)
    private double outcomingBufferCost;
    @Column(name = "tariff_duration_minutes", nullable = false)
    private Integer tariffDurationMinutes;
    @Column(name = "free_between_diff_providers", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean freeBetweenDifferentProviders;
    @Override
    public String toString() {
        return "";
    }
}
