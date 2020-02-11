package org.ebfhub.fastprotobuf;

import java.io.IOException;
import java.io.InputStream;

public class MutableByteArrayInputStream extends InputStream
{
    byte[] buf;
    int len;
    int pos;

    @Override
    public int read() throws IOException {
        if(pos==len){
            return -1;
        }
        return buf[pos++];
    }

    public void setBytes(byte[] tmp, int tmpLen) {
        buf=tmp;
        len=tmpLen;
        pos=0;
    }
}
