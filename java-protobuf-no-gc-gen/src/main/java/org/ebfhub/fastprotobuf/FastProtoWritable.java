package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedOutputStream;

/**
 * <p>FastProtoWritable interface.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public interface FastProtoWritable {
    /**
     * <p>write.</p>
     *
     * @param os a {@link com.google.protobuf.CodedOutputStream} object.
     * @param writer a {@link org.ebfhub.fastprotobuf.FastProtoWriter} object.
     * @throws java.io.IOException if any.
     */
    void write(CodedOutputStream os, FastProtoWriter writer) throws java.io.IOException;
}
