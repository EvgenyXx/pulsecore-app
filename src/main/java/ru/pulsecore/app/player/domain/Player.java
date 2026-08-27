package ru.pulsecore.app.player.domain;

import jakarta.persistence.*;
import lombok.*;

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



    @Column(name = "primary_league", length = 20)
    private String primaryLeague;

    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private boolean pushEnabled = true;

    @Column(name = "selected_halls", length = 500)
    @Builder.Default
    private String selectedHalls = "";



    private LocalDateTime createdAt;

    @Column(name = "is_blocked")
    @Builder.Default
    private boolean isBlocked = false;

    @Builder.Default
    private boolean notificationsEnabled = true;

    @Column(name = "live_selected_halls", length = 500)
    @Builder.Default
    private String liveSelectedHalls = "";

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public boolean hasActiveSubscription() {
        return subscription != null && subscription.isActiveNow();
    }

    public boolean isAdmin() {
        return roles.stream().anyMatch(r -> "ADMIN".equals(r.getName())
                || "ROLE_ADMIN".equals(r.getName()));
    }
}