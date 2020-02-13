package org.ebfhub.fastprotobuf;

import com.google.protobuf.WireFormat;

public class FastProtoField {
    public final int num;
    public final int bit;
    public final boolean repeated;
    public final WireFormat.FieldType ft;
    public final Class<?> clazz;
    public final String name;

    public FastProtoField(String name, int num, int bit, WireFormat.FieldType ft,boolean repeated, Class<?> clazz)
    {
        this.repeated=repeated;
        this.num=num;
        this.bit=bit;
        this.ft=ft;
        this.clazz=clazz;
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }
}
