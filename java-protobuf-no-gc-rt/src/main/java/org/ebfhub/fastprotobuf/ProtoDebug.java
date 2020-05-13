package org.ebfhub.fastprotobuf;

import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.WireFormat;

import java.io.IOException;

/**
 * <p>ProtoDebug class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class ProtoDebug
{
    static String INDENT="   ";
    /**
     * <p>decodeProto.</p>
     *
     * @param data an array of {@link byte} objects.
     * @param singleLine a boolean.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public static String decodeProto(byte[] data, boolean singleLine) throws IOException {
        return decodeProto(ByteString.copyFrom(data), 0, singleLine);
    }

    /**
     * <p>decodeProto.</p>
     *
     * @param data a {@link com.google.protobuf.ByteString} object.
     * @param depth a int.
     * @param singleLine a boolean.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public static String decodeProto(ByteString data, int depth, boolean singleLine) throws IOException {
        final CodedInputStream input = CodedInputStream.newInstance(data.asReadOnlyByteBuffer());
        return decodeProtoInput(input, depth, singleLine);
    }

    private static String decodeProtoInput(CodedInputStream input, int depth, boolean singleLine) throws IOException {
        StringBuilder s = new StringBuilder("{ ");
        boolean foundFields = false;
        while (true) {
            final int tag = input.readTag();
            int type = WireFormat.getTagWireType(tag);
            if (tag == 0 || type == WireFormat.WIRETYPE_END_GROUP) {
                break;
            }
            foundFields = true;
            protoNewline(depth, s, singleLine);

            final int number = WireFormat.getTagFieldNumber(tag);
            s.append(number).append(": ");

            switch (type) {
                case WireFormat.WIRETYPE_VARINT:
                    s.append(input.readInt64());
                    break;
                case WireFormat.WIRETYPE_FIXED64:
                    s.append(Double.longBitsToDouble(input.readFixed64()));
                    break;
                case WireFormat.WIRETYPE_LENGTH_DELIMITED:
                    ByteString data = input.readBytes();
                    try {
                        String submessage = decodeProto(data, depth + 1, singleLine);
                        if (data.size() < 30) {
                            boolean probablyString = true;
                            String str = new String(data.toByteArray(), Charsets.UTF_8);
                            for (char c : str.toCharArray()) {
                                if (c < '\n') {
                                    probablyString = false;
                                    break;
                                }
                            }
                            if (probablyString) {
                                s.append("\"").append(str).append("\" ");
                            }
                        }
                        s.append(submessage);
                    } catch (IOException e) {
                        s.append('"').append(new String(data.toByteArray())).append('"');
                    }
                    break;
                case WireFormat.WIRETYPE_START_GROUP:
                    s.append(decodeProtoInput(input, depth + 1, singleLine));
                    break;
                case WireFormat.WIRETYPE_FIXED32:
                    s.append(Float.intBitsToFloat(input.readFixed32()));
                    break;
                default:
                    throw new InvalidProtocolBufferException("Invalid wire type");
            }

        }
        if (foundFields) {
            protoNewline(depth - 1, s, singleLine);
        }
        return s.append('}').toString();
    }

    private static void protoNewline(int depth, StringBuilder s, boolean noNewline) {
        if (noNewline) {
            s.append(" ");
            return;
        }
        s.append('\n');
        for (int i = 0; i <= depth; i++) {
            s.append(INDENT);
        }
    }

}
