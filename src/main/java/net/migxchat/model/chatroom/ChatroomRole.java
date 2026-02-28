package net.migxchat.model.chatroom;

public enum ChatroomRole {
    OWNER(0, "Owner", true, true, true, true, true, true),
    ADMIN(1, "Admin", true, true, true, true, true, false),
    MODERATOR(2, "Moderator", true, true, true, false, false, false),
    MEMBER(3, "Member", false, false, false, false, false, false),
    GUEST(4, "Guest", false, false, false, false, false, false);

    private final int value;
    private final String displayName;
    private final boolean canKick;
    private final boolean canBan;
    private final boolean canMute;
    private final boolean canDeleteMessages;
    private final boolean canManageRoles;
    private final boolean canDeleteRoom;

    ChatroomRole(int value, String displayName, boolean canKick, boolean canBan,
                 boolean canMute, boolean canDeleteMessages, boolean canManageRoles, boolean canDeleteRoom) {
        this.value = value;
        this.displayName = displayName;
        this.canKick = canKick;
        this.canBan = canBan;
        this.canMute = canMute;
        this.canDeleteMessages = canDeleteMessages;
        this.canManageRoles = canManageRoles;
        this.canDeleteRoom = canDeleteRoom;
    }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }
    public boolean isCanKick() { return canKick; }
    public boolean isCanBan() { return canBan; }
    public boolean isCanMute() { return canMute; }
    public boolean isCanDeleteMessages() { return canDeleteMessages; }
    public boolean isCanManageRoles() { return canManageRoles; }
    public boolean isCanDeleteRoom() { return canDeleteRoom; }

    public boolean isHigherThan(ChatroomRole other) {
        return this.value < other.value;
    }

    public boolean isAtLeast(ChatroomRole role) {
        return this.value <= role.value;
    }
}
