package net.migxchat.protocol;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FusionPacket {
    private static final int MAX_PACKET_SIZE = 65536;

    private PacketType type;
    private int sequenceNumber;
    private final Map<Short, PacketField> fields = new HashMap<>();

    public FusionPacket() {}

    public FusionPacket(PacketType type) {
        this.type = type;
    }

    public PacketType getType() { return type; }
    public void setType(PacketType type) { this.type = type; }
    public int getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(int sequenceNumber) { this.sequenceNumber = sequenceNumber; }
    public Map<Short, PacketField> getFields() { return fields; }

    public void addField(short fieldId, String value) {
        fields.put(fieldId, new PacketField(fieldId, PacketField.FieldType.STRING, value));
    }

    public void addField(short fieldId, int value) {
        fields.put(fieldId, new PacketField(fieldId, PacketField.FieldType.INT, value));
    }

    public void addField(short fieldId, long value) {
        fields.put(fieldId, new PacketField(fieldId, PacketField.FieldType.LONG, value));
    }

    public void addField(short fieldId, boolean value) {
        fields.put(fieldId, new PacketField(fieldId, PacketField.FieldType.BOOLEAN, value));
    }

    public void addField(short fieldId, byte[] value) {
        fields.put(fieldId, new PacketField(fieldId, PacketField.FieldType.BYTE_ARRAY, value));
    }

    public void addField(short fieldId, short value) {
        fields.put(fieldId, new PacketField(fieldId, PacketField.FieldType.SHORT, value));
    }

    @SuppressWarnings("unchecked")
    public <T> T getField(short fieldId, Class<T> clazz) {
        PacketField field = fields.get(fieldId);
        if (field == null) return null;
        return clazz.cast(field.getValue());
    }

    public String getStringField(short fieldId) {
        return getField(fieldId, String.class);
    }

    public Integer getIntField(short fieldId) {
        return getField(fieldId, Integer.class);
    }

    public Long getLongField(short fieldId) {
        return getField(fieldId, Long.class);
    }

    public Boolean getBooleanField(short fieldId) {
        return getField(fieldId, Boolean.class);
    }

    public byte[] serialize() {
        ByteBuffer fieldsBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);
        for (PacketField field : fields.values()) {
            fieldsBuffer.putShort(field.getFieldId());
            fieldsBuffer.put((byte) field.getType().getCode());
            switch (field.getType()) {
                case BYTE -> fieldsBuffer.put((byte) 1).put(((Number) field.getValue()).byteValue());
                case SHORT -> fieldsBuffer.putShort((short) Short.BYTES).putShort(((Number) field.getValue()).shortValue());
                case INT -> fieldsBuffer.putInt(Integer.BYTES).putInt(((Number) field.getValue()).intValue());
                case LONG -> fieldsBuffer.putLong((long) Long.BYTES).putLong(((Number) field.getValue()).longValue());
                case BOOLEAN -> fieldsBuffer.put((byte) 1).put((byte) ((Boolean) field.getValue() ? 1 : 0));
                case STRING -> {
                    byte[] strBytes = ((String) field.getValue()).getBytes(StandardCharsets.UTF_8);
                    fieldsBuffer.putShort((short) strBytes.length).put(strBytes);
                }
                case BYTE_ARRAY -> {
                    byte[] arr = (byte[]) field.getValue();
                    fieldsBuffer.putInt(arr.length).put(arr);
                }
            }
        }
        fieldsBuffer.flip();
        int fieldsLen = fieldsBuffer.limit();

        // Header: type(1) + length(4) + seq(4) = 9 bytes
        ByteBuffer result = ByteBuffer.allocate(9 + fieldsLen);
        result.put((byte) type.getValue());
        result.putInt(fieldsLen + 4); // length includes seq number
        result.putInt(sequenceNumber);
        result.put(fieldsBuffer);
        return result.array();
    }

    public static FusionPacket deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        FusionPacket packet = new FusionPacket();
        packet.type = PacketType.fromValue(buffer.get() & 0xFF);
        int length = buffer.getInt();
        packet.sequenceNumber = buffer.getInt();
        int fieldsLength = length - 4;
        int fieldsEnd = 9 + fieldsLength;
        while (buffer.position() < fieldsEnd && buffer.hasRemaining()) {
            short fieldId = buffer.getShort();
            PacketField.FieldType fieldType = PacketField.FieldType.fromCode(buffer.get() & 0xFF);
            Object value;
            switch (fieldType) {
                case BYTE -> { buffer.get(); value = buffer.get(); }
                case SHORT -> { buffer.getShort(); value = buffer.getShort(); }
                case INT -> { buffer.getInt(); value = buffer.getInt(); }
                case LONG -> { buffer.getLong(); value = buffer.getLong(); }
                case BOOLEAN -> { buffer.get(); value = buffer.get() != 0; }
                case STRING -> {
                    short strLen = buffer.getShort();
                    byte[] strBytes = new byte[strLen];
                    buffer.get(strBytes);
                    value = new String(strBytes, StandardCharsets.UTF_8);
                }
                case BYTE_ARRAY -> {
                    int arrLen = buffer.getInt();
                    byte[] arr = new byte[arrLen];
                    buffer.get(arr);
                    value = arr;
                }
                default -> throw new IllegalStateException("Unknown field type");
            }
            packet.fields.put(fieldId, new PacketField(fieldId, fieldType, value));
        }
        return packet;
    }
}
