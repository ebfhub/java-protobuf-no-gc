package org.ebfhub.fastprotobuf;

import com.google.protobuf.WireFormat;

/**
 * <p>FastProtoField class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class FastProtoField {
    public final int num;
    public final int bit;
    public final boolean repeated;
    public final WireFormat.FieldType ft;
    public final Class<?> clazz;
    public final String name;

    /**
     * <p>Constructor for FastProtoField.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param num a int.
     * @param bit a int.
     * @param ft a {@link com.google.protobuf.WireFormat.FieldType} object.
     * @param repeated a boolean.
     * @param clazz a {@link java.lang.Class} object.
     */
    public FastProtoField(String name, int num, int bit, WireFormat.FieldType ft,boolean repeated, Class<?> clazz)
    {
        this.repeated=repeated;
        this.num=num;
        this.bit=bit;
        this.ft=ft;
        this.clazz=clazz;
        this.name=name;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }
}
