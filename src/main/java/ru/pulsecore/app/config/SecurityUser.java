package ru.pulsecore.app.config;

import ru.pulsecore.app.modules.player.domain.Player;
import lombok.Getter;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

@Getter
public class SecurityUser extends User {

    private final String playerId;
    private final String playerName;
    private final String email;
    private final boolean isAdmin;

    public SecurityUser(Player player) {
        super(
                player.getEmail() != null ? player.getEmail() : player.getName(),
                player.getPassword(),
                !player.isBlocked(),
                true, true, true,
                player.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
        this.playerId = player.getId().toString();
        this.playerName = player.getName();
        this.email = player.getEmail();
        this.isAdmin = player.isAdmin();
    }
}