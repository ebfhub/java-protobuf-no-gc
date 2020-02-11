package org.ebfhub.fastprotobuf;

import java.io.ByteArrayOutputStream;

public class ReusableByteArrayOutputStream extends ByteArrayOutputStream
{
    public byte[] getBytes(){
        return buf;
    }

}
