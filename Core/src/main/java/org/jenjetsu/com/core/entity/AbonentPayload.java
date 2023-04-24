package org.jenjetsu.com.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "abonent_payload")
public class AbonentPayload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payloadId;
    @ManyToOne
    @JoinColumn(name = "abonent_id")
    private Abonent abonent;
    @Column(name = "call_type")
    private Byte callType;
    @Column(name = "start_time")
    private Timestamp startTime;
    @Column(name = "end_time")
    private Timestamp endTime;
    @Column(name = "duration")
    private Time duration;
    @Column(name = "cost", scale = 2)
    private double cost;

    @Override
    @SneakyThrows
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        SimpleDateFormat durationFormatter = new SimpleDateFormat("HH:mm:ss");
        return String.format("%d,%s,%s,%s,%s", callType, dateFormat.format(startTime), dateFormat.format(endTime),
                durationFormatter.format(duration), Double.toString(cost).replace(",","."));
    }
}
