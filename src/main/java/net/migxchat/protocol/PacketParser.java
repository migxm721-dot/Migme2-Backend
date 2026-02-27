package net.migxchat.protocol;

import io.netty.buffer.ByteBuf;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PacketParser {

    public FusionPacket parse(ByteBuf buf) {
        if (buf.readableBytes() < 9) {
            return null;
        }
        buf.markReaderIndex();
        int typeVal = buf.readUnsignedByte();
        long length = buf.readUnsignedInt();
        if (buf.readableBytes() < length) {
            buf.resetReaderIndex();
            return null;
        }
        FusionPacket packet = new FusionPacket();
        packet.setType(PacketType.fromValue(typeVal));
        packet.setSequenceNumber(buf.readInt());
        int fieldsLength = (int) (length - 4);
        int endIndex = buf.readerIndex() + fieldsLength;

        while (buf.readerIndex() < endIndex && buf.isReadable()) {
            short fieldId = buf.readShort();
            int fieldTypeCode = buf.readUnsignedByte();
            PacketField.FieldType fieldType = PacketField.FieldType.fromCode(fieldTypeCode);
            switch (fieldType) {
                case BYTE -> {
                    buf.readByte(); // length=1
                    packet.getFields().put(fieldId, new PacketField(fieldId, fieldType, buf.readByte()));
                }
                case SHORT -> {
                    buf.readShort(); // length=2
                    packet.getFields().put(fieldId, new PacketField(fieldId, fieldType, buf.readShort()));
                }
                case INT -> {
                    buf.readInt(); // length=4
                    packet.getFields().put(fieldId, new PacketField(fieldId, fieldType, buf.readInt()));
                }
                case LONG -> {
                    buf.readLong(); // length=8
                    packet.getFields().put(fieldId, new PacketField(fieldId, fieldType, buf.readLong()));
                }
                case BOOLEAN -> {
                    buf.readByte(); // length=1
                    packet.getFields().put(fieldId, new PacketField(fieldId, fieldType, buf.readByte() != 0));
                }
                case STRING -> {
                    int strLen = buf.readUnsignedShort();
                    String str = buf.readCharSequence(strLen, StandardCharsets.UTF_8).toString();
                    packet.getFields().put(fieldId, new PacketField(fieldId, fieldType, str));
                }
                case BYTE_ARRAY -> {
                    int arrLen = buf.readInt();
                    byte[] arr = new byte[arrLen];
                    buf.readBytes(arr);
                    packet.getFields().put(fieldId, new PacketField(fieldId, fieldType, arr));
                }
            }
        }
        return packet;
    }
}
