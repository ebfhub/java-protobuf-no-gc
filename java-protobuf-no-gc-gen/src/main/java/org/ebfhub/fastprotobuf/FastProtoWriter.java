package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>FastProtoWriter class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class FastProtoWriter {
    // TODO allow for longer
    private byte[] buf = new byte[1024];

    final Utf8.ByteWriter writer = new Utf8.ByteWriter() {
        @Override
        void putByte(Object src, long offset, byte b) {
            ((byte[])src)[(int)offset]=b;
        }
    };
    /**
     * <p>writeString.</p>
     *
     * @param field a int.
     * @param os a {@link com.google.protobuf.CodedOutputStream} object.
     * @param str a {@link java.lang.CharSequence} object.
     * @throws java.io.IOException if any.
     */
    public void writeString(int field, CodedOutputStream os, CharSequence str) throws IOException {
        long len = Utf8.putCharsToUtf8(0, str, buf.length, 0, buf, writer);
        os.writeTag(field, WireFormat.WIRETYPE_LENGTH_DELIMITED);
        os.writeUInt32NoTag((int)len);
        os.writeLazy(buf, 0, (int)len);
    }

    private static class Helper
    {
        // TODO move to no-sync reusable buffer
        ByteArrayOutputStream bos = new ByteArrayOutputStream(){
            @Override
            public synchronized byte[] toByteArray() {
                return buf;
            }
        };
        CodedOutputStream os = CodedOutputStream.newInstance(bos);
    }
    private List<Helper> pool=new ArrayList<>();

    /**
     * <p>writeMessage.</p>
     *
     * @param field a int.
     * @param os a {@link com.google.protobuf.CodedOutputStream} object.
     * @param i a int.
     * @throws java.io.IOException if any.
     */
    public void writeMessage( int field, CodedOutputStream os, int i) throws IOException {
        os.writeInt32(field,i);
    }

    /**
     * <p>writeMessage.</p>
     *
     * @param field a int.
     * @param os a {@link com.google.protobuf.CodedOutputStream} object.
     * @param w a {@link org.ebfhub.fastprotobuf.FastProtoWritable} object.
     * @throws java.io.IOException if any.
     */
    public void writeMessage( int field, CodedOutputStream os, FastProtoWritable w) throws IOException {
        Helper h = pool.size()==0?new Helper():pool.remove(pool.size()-1);
        try {
            h.bos.reset();

            w.write(h.os, this);
            h.os.flush();

            int bytes = h.bos.size();

            os.writeTag(field, WireFormat.WIRETYPE_LENGTH_DELIMITED);
            os.writeUInt32NoTag(bytes);

            os.writeLazy(h.bos.toByteArray(), 0, bytes);
        }
        finally {
            pool.add(h);
        }
    }
}
