package net.migxchat.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PacketField {
    private short fieldId;
    private FieldType type;
    private Object value;

    public enum FieldType {
        BYTE(1),
        SHORT(2),
        INT(3),
        LONG(4),
        STRING(5),
        BOOLEAN(6),
        BYTE_ARRAY(7);

        private final int code;

        FieldType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static FieldType fromCode(int code) {
            for (FieldType ft : values()) {
                if (ft.code == code) return ft;
            }
            throw new IllegalArgumentException("Unknown field type code: " + code);
        }
    }
}
