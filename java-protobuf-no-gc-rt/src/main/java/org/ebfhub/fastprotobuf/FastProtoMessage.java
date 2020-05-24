package org.ebfhub.fastprotobuf;

public interface FastProtoMessage {
    FastProtoSetter getSetter();

    void clear();

    /**
     * Free and object and all children
     */
    void release();
}
