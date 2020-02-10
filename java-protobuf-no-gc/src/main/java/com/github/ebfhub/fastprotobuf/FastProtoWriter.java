package com.github.ebfhub.fastprotobuf;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import sun.misc.Unsafe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FastProtoWriter {
    private byte[] buf = new byte[1024];

    public void writeString(CodedOutputStream os, int field, CharSequence str) throws IOException {
        long len = Utf8.putCharsToUtf8(0,str,buf.length, Unsafe.ARRAY_BYTE_BASE_OFFSET,buf);
        os.writeTag(field, WireFormat.WIRETYPE_LENGTH_DELIMITED);
        os.writeUInt32NoTag((int)len);
        os.writeLazy(buf, 0, (int)len);
    }

    private static class Helper
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(){
            @Override
            public synchronized byte[] toByteArray() {
                return buf;
            }
        };
        CodedOutputStream os = CodedOutputStream.newInstance(bos);
    }
    private List<Helper> pool=new ArrayList<>();

    public void writeMessage( int field, CodedOutputStream os, FastProtoWritable w) throws IOException {
        Helper h = pool.size()==0?new Helper():pool.remove(0);
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
