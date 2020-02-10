package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedOutputStream;

public interface FastProtoWritable {
    void write(CodedOutputStream os, FastProtoWriter writer) throws java.io.IOException;
}
