package org.ebfhub.fastprotobuf;

import java.util.List;

/**
 * <p>FastProtoSetter interface.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public interface FastProtoSetter {

    /**
     * Set an integer value directly
     *
     * @param field a int.
     * @param value a int.
     */
    void field_set(int field, int value);

    /**
     * Set a long value directly
     *
     * @param field a int.
     * @param value a long.
     */
    void field_set(int field, long value);

    /**
     * Set a double value directly
     *
     * @param field a int.
     * @param value a double.
     */
    void field_set(int field, double value);

    /**
     * Set a float value directly
     *
     * @param field a int.
     * @param value a float.
     */
    void field_set(int field, float value);
    /**
     * Set a boolean value directly
     *
     * @param field a int.
     * @param value a boolean.
     */
    void field_set(int field, boolean value);

    /**
     * Mark a field as set and return its empty string builder.
     *
     * @param field a int.
     * @param pool a {@link org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool} object.
     * @return a {@link java.lang.StringBuilder} object.
     */
    StringBuilder field_builder(int field, FastProtoReader.ObjectPool pool);

    /**
     * Add a message field, returning a setter component
     *
     * @param field a int.
     * @param pool a {@link org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool} object.
     * @return a {@link org.ebfhub.fastprotobuf.FastProtoSetter} object.
     */
    FastProtoSetter field_add(int field, FastProtoReader.ObjectPool pool);

    /**
     * Get definition for a field.
     *
     * @param field a int.
     * @return a {@link org.ebfhub.fastprotobuf.FastProtoField} object.
     */
    FastProtoField field_getDef(int field);

    /**
     * Get list of fields.
     *
     * @return a {@link java.util.List} object.
     */
    List<FastProtoField> field_getAll();

    /**
     * Clear the object
     *
     * @param pool a {@link org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool} object.
     */
    void clear(FastProtoReader.ObjectPool pool);

}
