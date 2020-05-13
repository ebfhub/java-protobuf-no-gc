package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

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
    private byte[] buf = new byte[1024];

    final Utf8.ByteWriter writer = new Utf8.ByteWriter() {
        @Override
        void putByte(Object src, long offset, byte b) {
            if(offset==buf.length){
                byte[]buf2 = new byte[buf.length*2];
                System.arraycopy(buf,0,buf2,0,buf.length);
                buf=buf2;
            }
            buf[(int)offset]=b;
        }
    };

    public int getStringBufferLen(){
        return buf.length;
    }

    public void setStringBuffer(byte[]b){
        buf = b;
    }

    /**
     * <p>writeString.</p>
     *
     * @param field a int.
     * @param os a {@link com.google.protobuf.CodedOutputStream} object.
     * @param str a {@link java.lang.CharSequence} object.
     * @throws java.io.IOException if any.
     */
    public void writeString(int field, CodedOutputStream os, CharSequence str) throws IOException {
        long len = Utf8.putCharsToUtf8(0, str, Integer.MAX_VALUE, 0, buf, writer);
        os.writeTag(field, WireFormat.WIRETYPE_LENGTH_DELIMITED);
        os.writeUInt32NoTag((int)len);
        os.writeLazy(buf, 0, (int)len);
    }

    private static class Helper
    {
        ReusableByteArrayOutputStream bos = new ReusableByteArrayOutputStream();
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

            os.writeLazy(h.bos.getBytes(), 0, bytes);
        }
        finally {
            pool.add(h);
        }
    }
}
