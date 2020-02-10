package com.github.ebfhub.fastprotobuf;

import com.google.protobuf.CodedOutputStream;

public interface FastProtoWritable {
    void write(CodedOutputStream os, com.github.ebfhub.fastprotobuf.FastProtoWriter writer) throws java.io.IOException;

}
