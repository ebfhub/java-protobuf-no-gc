package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedInputStream;

public class FastProtoMutableCodedInputStream {
    private final MutableByteArrayInputStream mis;
    private final CodedInputStream is3;

    public FastProtoMutableCodedInputStream(){
        mis = new MutableByteArrayInputStream();
        is3 = CodedInputStream.newInstance(mis);
    }

    public void setBytes(byte[] b, int offset, int len){
        mis.setBytes(b,offset,len);
    }

    public CodedInputStream getCodedInputStream(){
        return is3;
    }

    public void reset() {
        is3.resetSizeCounter();
    }
}
