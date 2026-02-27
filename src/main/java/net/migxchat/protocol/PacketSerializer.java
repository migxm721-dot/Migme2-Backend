package net.migxchat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PacketSerializer {

    public ByteBuf serialize(FusionPacket packet) {
        ByteBuf fieldsBuf = Unpooled.buffer();
        for (PacketField field : packet.getFields().values()) {
            fieldsBuf.writeShort(field.getFieldId());
            fieldsBuf.writeByte(field.getType().getCode());
            switch (field.getType()) {
                case BYTE -> fieldsBuf.writeByte(Byte.BYTES).writeByte(((Number) field.getValue()).byteValue());
                case SHORT -> fieldsBuf.writeShort(Short.BYTES).writeShort(((Number) field.getValue()).shortValue());
                case INT -> fieldsBuf.writeInt(Integer.BYTES).writeInt(((Number) field.getValue()).intValue());
                case LONG -> fieldsBuf.writeLong(Long.BYTES).writeLong(((Number) field.getValue()).longValue());
                case BOOLEAN -> fieldsBuf.writeByte(1).writeByte((Boolean) field.getValue() ? 1 : 0);
                case STRING -> {
                    byte[] strBytes = ((String) field.getValue()).getBytes(StandardCharsets.UTF_8);
                    fieldsBuf.writeShort(strBytes.length).writeBytes(strBytes);
                }
                case BYTE_ARRAY -> {
                    byte[] arr = (byte[]) field.getValue();
                    fieldsBuf.writeInt(arr.length).writeBytes(arr);
                }
            }
        }
        int fieldsLen = fieldsBuf.readableBytes();
        ByteBuf result = Unpooled.buffer(9 + fieldsLen);
        result.writeByte(packet.getType().getValue());
        result.writeInt(fieldsLen + 4);
        result.writeInt(packet.getSequenceNumber());
        result.writeBytes(fieldsBuf);
        fieldsBuf.release();
        return result;
    }
}
