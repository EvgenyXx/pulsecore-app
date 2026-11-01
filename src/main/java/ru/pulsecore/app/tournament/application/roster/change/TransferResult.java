package ru.pulsecore.app.tournament.application.roster.change;

public record TransferResult(
        TransferStatus status,
        TransferInfo info
) {
    public static TransferResult transferred(TransferInfo info) {
        return new TransferResult(TransferStatus.TRANSFERRED, info);
    }

    public static TransferResult removed() {
        return new TransferResult(TransferStatus.REMOVED, null);
    }
}