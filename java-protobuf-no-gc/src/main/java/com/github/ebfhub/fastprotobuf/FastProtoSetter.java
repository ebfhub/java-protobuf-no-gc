package com.github.ebfhub.fastprotobuf;

public interface FastProtoSetter {
    void field_set(int field, int value);
    void field_set(int field, long value);
    void field_set(int field, double value);
    void field_set(int field, float value);
    void field_set(int field, boolean value);
    void field_set(int field, CharSequence value);
    StringBuilder field_builder(int field);

    FastProtoSetter field_add(int field);
    FastProtoField getField(int field);
}
