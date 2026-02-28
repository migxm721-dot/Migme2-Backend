package net.migxchat.model.chatroom;

public enum BanDuration {
    MINUTES_5(5 * 60, "5 minutes"),
    MINUTES_15(15 * 60, "15 minutes"),
    HOURS_1(60 * 60, "1 hour"),
    HOURS_24(24 * 60 * 60, "24 hours"),
    DAYS_7(7 * 24 * 60 * 60, "7 days"),
    DAYS_30(30 * 24 * 60 * 60, "30 days"),
    PERMANENT(-1, "Permanent");

    private final int seconds;
    private final String displayName;

    BanDuration(int seconds, String displayName) {
        this.seconds = seconds;
        this.displayName = displayName;
    }

    public int getSeconds() { return seconds; }
    public String getDisplayName() { return displayName; }
    public boolean isPermanent() { return seconds == -1; }
}
