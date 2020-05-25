package org.ebfhub.fastprotobuf;

public interface FastProtoMessage {
    FastProtoSetter getSetter();

    void clear();

    /**
     * Free and object and all children
     */
    void release();

    /**
     * Set the reference count
     * Should not be called manually.
     */
    void setRefCount(int n);
}
