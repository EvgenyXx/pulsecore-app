package ru.pulsecore.app.tournament.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hall_stream")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HallStream {
    @Id
    private String hall;

    @Column(name = "stream_url", nullable = false)
    private String streamUrl;
}