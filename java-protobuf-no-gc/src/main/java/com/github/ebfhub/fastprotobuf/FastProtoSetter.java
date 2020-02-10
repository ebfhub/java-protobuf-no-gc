package com.github.ebfhub.fastprotobuf;

public interface FastProtoSetter {

    /**
     * Set an integer value directly
     */
    void field_set(int field, int value);

    /**
     * Set a long value directly
     */
    void field_set(int field, long value);

    /**
     * Set a double value directly
     */
    void field_set(int field, double value);

    /**
     * Set a float value directly
     */

    void field_set(int field, float value);
    /**
     * Set a boolean value directly
     */
    void field_set(int field, boolean value);

    /**
     * Mark a field as set and return its empty string builder.
     */
    StringBuilder field_builder(int field);

    /**
     * Add a message field, returning a setter component
     */
    FastProtoSetter field_add(int field);

    /**
     * Get definition for a field.
     */
    FastProtoField getField(int field);
}
