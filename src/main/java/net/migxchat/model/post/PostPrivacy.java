package net.migxchat.model.post;

public enum PostPrivacy {
    PUBLIC(1),
    FRIENDS(2),
    PRIVATE(3);

    private final int value;

    PostPrivacy(int value) {
        this.value = value;
    }

    public int getValue() { return value; }

    public static PostPrivacy fromValue(int value) {
        for (PostPrivacy p : values()) {
            if (p.value == value) return p;
        }
        return PUBLIC;
    }
}
