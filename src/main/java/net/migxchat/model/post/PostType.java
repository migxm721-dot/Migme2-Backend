package net.migxchat.model.post;

public enum PostType {
    TEXT(1),
    LINK(2),
    PHOTO(4),
    VIDEO(8),
    RSS(16),
    ACTIVITY(32),
    GAME(64);

    private final int value;

    PostType(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    public static PostType fromValue(int value) {
        for (PostType type : values()) {
            if (type.value == value) return type;
        }
        return TEXT;
    }
}
