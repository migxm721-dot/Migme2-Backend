package net.migxchat.protocol;

public enum PacketType {
    UNKNOWN(0),
    LOGIN(1),
    LOGIN_OK(2),
    MESSAGE(3),
    PRESENCE(4),
    STATUS_MESSAGE(5),
    DISPLAY_PICTURE(6),
    CONTACT(7),
    REMOVE_CONTACT(8),
    GROUP(9),
    REMOVE_GROUP(10),
    CONTACT_LIST_VERSION(11),
    ACCOUNT_BALANCE(12),
    EMOTICON_HOTKEYS(13),
    AVATAR(14),
    JOIN_CHATROOM(15),
    LEAVE_CHATROOM(16),
    CHATROOM_USER_STATUS(17),
    GET_CHATROOM_PARTICIPANTS(18),
    GROUP_CHAT_PARTICIPANTS(19),
    GROUP_CHAT_USER_STATUS(20),
    MESSAGE_STATUS_EVENT(21),
    CHAT(22),
    CHAT_LIST_VERSION(23),
    LATEST_MESSAGES_DIGEST(24),
    CAPTCHA(25),
    IM_AVAILABLE(26),
    IM_SESSION_STATUS(27),
    CHATROOM_NOTIFICATION(28),
    SET_CHAT_MUTING(29),
    NO_PINNED_MESSAGE(30),
    NOTIFICATION(31),
    GET_URL(32),
    LOGOUT(33),
    PING(34),
    PONG(35),
    ERROR(36),
    REGISTER(37),
    UPDATE_PROFILE(38),
    ADD_CONTACT(39),
    BLOCK_CONTACT(40);

    private final int value;

    PacketType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PacketType fromValue(int value) {
        for (PacketType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
