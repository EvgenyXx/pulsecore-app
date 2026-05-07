package ru.pulsecore.app.modules.player.api;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.pulsecore.app.core.dto.TopPlayerProjection;
import ru.pulsecore.app.modules.player.api.dto.NotificationsStatusResponse;
import ru.pulsecore.app.modules.auth.api.dto.ChangePasswordRequest;
import ru.pulsecore.app.modules.auth.api.dto.UpdateProfileRequest;
import ru.pulsecore.app.modules.payment.YookassaService;

import ru.pulsecore.app.modules.player.api.dto.*;

import ru.pulsecore.app.modules.player.domain.Subscription;
import ru.pulsecore.app.modules.player.service.PlayerService;
import ru.pulsecore.app.modules.player.service.PlayerStatsService;
import ru.pulsecore.app.modules.player.service.RoleManagementService;
import ru.pulsecore.app.modules.player.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pulsecore.app.modules.shared.SessionProperties;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(PlayerApi.BASE_PATH)
@RequiredArgsConstructor
public class PlayerApiController {

    private final PlayerService playerService;
    private final PlayerStatsService playerStatsService;
    private final SubscriptionService subscriptionService;
    private final YookassaService yookassaService;
    private final RoleManagementService roleManagementService;
    private final SessionProperties sessionProperties;



    @GetMapping(PlayerApi.TOP_WEEK_POSITION)
    public ResponseEntity<TopWeekResponse> getTopWeekPosition(@PathVariable UUID id) {
        return ResponseEntity.ok(playerStatsService.getTopWithPosition(id));
    }


    @GetMapping(PlayerApi.TOP_WEEK)
    public ResponseEntity<List<TopPlayerProjection>> getTopWeek() {
        return ResponseEntity.ok(playerStatsService.getTopPlayers());
    }

    @GetMapping(PlayerApi.DASHBOARD)
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(playerStatsService.getDashboard(id));
    }

    @GetMapping(PlayerApi.SUM)
    public ResponseEntity<SumResponse> getSumById(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(playerStatsService.getSum(id, start, end));
    }

    @PutMapping(PlayerApi.PROFILE)
    public ResponseEntity<PlayerProfileResponse> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(playerService.updateProfile(id, request));
    }

    @PutMapping(PlayerApi.CHANGE_PASSWORD)
    public ResponseEntity<MessageResponse> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request) {
        playerService.changePassword(id, request);
        return ResponseEntity.ok(new MessageResponse("Пароль изменён"));
    }

    @PostMapping(PlayerApi.SUBSCRIBE)
    public ResponseEntity<MessageResponse> subscribe(@PathVariable UUID id, @RequestParam(defaultValue = "30") int days) {
        subscriptionService.activate(id, days);
        return ResponseEntity.ok(new MessageResponse("Подписка активирована на " + days + " дней"));
    }

    @DeleteMapping(PlayerApi.UNSUBSCRIBE)
    public ResponseEntity<MessageResponse> unsubscribe(@PathVariable UUID id) {
        subscriptionService.deactivate(id);
        return ResponseEntity.ok(new MessageResponse("Подписка отключена"));
    }

    @GetMapping(PlayerApi.SEARCH)
    public ResponseEntity<List<PlayerResponse>> search(@RequestParam(PlayerApi.SEARCH_PARAM) String q) {
        return ResponseEntity.ok(playerService.searchPlayers(q));
    }

    @GetMapping(PlayerApi.SUBSCRIPTION)
    public ResponseEntity<SubscriptionStatusResponse> getSubscription(@PathVariable UUID id) {
        Subscription sub = subscriptionService.getByPlayerId(id);
        if (sub == null) return ResponseEntity.ok(new SubscriptionStatusResponse(false, null, null));
        return ResponseEntity.ok(SubscriptionStatusResponse.builder()
                .active(sub.isActiveNow())
                .expiresAt(sub.getExpiresAt() != null ? sub.getExpiresAt().toString() : null)
                .startedAt(sub.getStartedAt() != null ? sub.getStartedAt().toString() : null)
                .build());
    }

    @DeleteMapping(PlayerApi.DELETE_ACCOUNT)
    public ResponseEntity<MessageResponse> deleteAccount(@PathVariable UUID id,
                                                         HttpSession session,
                                                         HttpServletResponse response) {
        playerService.deletePlayer(id);
        session.invalidate();
        SecurityContextHolder.clearContext();

        Cookie cookie = new Cookie(sessionProperties.getName(), null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok(new MessageResponse("Аккаунт удалён"));
    }

    @PostMapping(PlayerApi.PAY)
    public ResponseEntity<PaymentResponse> pay(@PathVariable UUID id, @RequestParam int months) {
        var payment = yookassaService.createPayment(id, months);
        return ResponseEntity.ok(new PaymentResponse(payment.confirmationUrl()));
    }

    @PostMapping(PlayerApi.GRANT_ROLE)
    public ResponseEntity<MessageResponse> grantRole(@PathVariable UUID id, @RequestParam String role) {
        roleManagementService.grantRole(id, role);
        return ResponseEntity.ok(new MessageResponse("Роль " + role + " выдана"));
    }

    @DeleteMapping(PlayerApi.REVOKE_ROLE)
    public ResponseEntity<MessageResponse> revokeRole(@PathVariable UUID id, @RequestParam String role) {
        roleManagementService.revokeRole(id, role);
        return ResponseEntity.ok(new MessageResponse("Роль " + role + " отозвана"));
    }

    @GetMapping(PlayerApi.ROLES)
    public ResponseEntity<List<String>> getRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(roleManagementService.getRoleNames(id));
    }

    @PutMapping(PlayerApi.NOTIFICATIONS)
    public ResponseEntity<MessageResponse> toggleNotifications(
            @PathVariable UUID id, @RequestParam boolean enabled) {
        playerService.setNotificationsEnabled(id, enabled);
        return ResponseEntity.ok(new MessageResponse(enabled ? "Уведомления включены" : "Уведомления отключены"));
    }

    @GetMapping(PlayerApi.NOTIFICATIONS_STATUS)
    public ResponseEntity<NotificationsStatusResponse> getNotificationsStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(new NotificationsStatusResponse(playerService.isNotificationsEnabled(id)));
    }
}