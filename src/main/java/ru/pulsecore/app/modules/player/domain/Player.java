package ru.pulsecore.app.modules.player.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.pulsecore.app.modules.notification.domain.PlayerNotification;
import ru.pulsecore.app.modules.tournament.persistence.entity.TournamentResultEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column(unique = true, length = 10)
    private String accessCode;

    @Column(length = 6)
    private String verificationCode;

    @Builder.Default
    private boolean verified = false;

    // ── OAuth поля ──
    @Column(name = "oauth_provider", length = 50)
    private String oauthProvider;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "gender", length = 10)
    private String gender;
    // ── конец OAuth ──

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "player_roles",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subscription subscription;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentResultEntity> results = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerNotification> notifications = new ArrayList<>();



    private LocalDateTime createdAt;

    @Column(name = "is_blocked")
    @Builder.Default
    private boolean isBlocked = false;

    @Builder.Default
    private boolean notificationsEnabled = true;

    public boolean hasActiveSubscription() {
        return subscription != null && subscription.isActiveNow();
    }

    public boolean isAdmin() {
        return roles.stream().anyMatch(r -> "ADMIN".equals(r.getName())
                || "ROLE_ADMIN".equals(r.getName()));
    }
}