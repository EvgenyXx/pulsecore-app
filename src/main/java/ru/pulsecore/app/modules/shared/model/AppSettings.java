package ru.pulsecore.app.modules.shared.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Column(unique = true, nullable = false, length = 100)
    private String key;

    @Column(nullable = false, length = 500)
    private String value;
}