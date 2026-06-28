// ==================== SumResponse.java — заменить ====================
package ru.pulsecore.app.modules.player.api.dto.sum;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SumResponse {
    private String playerName;
    private String start;
    private String end;
    private Double sum;
    private Double average;
    private Long count;
    private List<TournamentItem> tournaments;
    private int totalPages;
    private int currentPage;
    private long totalElements;

    @Data
    @Builder
    public static class TournamentItem {
        private String date;
        private Double amount;
        private Long resultId;
        private boolean hasRemoved;
    }
}