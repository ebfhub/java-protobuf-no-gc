package org.ebfhub.fastprotobuf;

public interface FastProtoMessage {
    void clear();
    FastProtoSetter getSetter();
}
