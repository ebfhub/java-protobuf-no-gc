package com.github.ebfhub.fastprotobuf;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import sun.misc.Unsafe;

import java.io.IOException;

public class FastProtoWriter {
    byte[] buf = new byte[1024];
    public void writeString(CodedOutputStream os, int field, CharSequence str) throws IOException {

        long len = Utf8.putCharsToUtf8(0,str,buf.length, Unsafe.ARRAY_BYTE_BASE_OFFSET,buf);
        os.writeTag(field, WireFormat.WIRETYPE_LENGTH_DELIMITED);
        os.writeInt32NoTag((int)len);
        for(int n=0;n<len;n++){
            os.write(buf[n]);
        }
    }
}
