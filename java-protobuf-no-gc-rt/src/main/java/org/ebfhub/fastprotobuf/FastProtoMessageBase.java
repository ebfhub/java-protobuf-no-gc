package org.ebfhub.fastprotobuf;

import com.google.protobuf.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public abstract class FastProtoMessageBase<T extends FastProtoMessage> implements FastProtoMessage,FastProtoWritable, com.google.protobuf.Message {
    protected int fieldsSet=0;
    protected org.ebfhub.fastprotobuf.FastProtoObjectPool pool;

    /** Create default instance - used by GRPC */
    @SuppressWarnings("WeakerAccess")
    protected static ThreadLocal<org.ebfhub.fastprotobuf.FastProtoReader> DEFAULT_PARSER=ThreadLocal.withInitial(FastProtoReader::new);

    @SuppressWarnings("WeakerAccess")
    protected static ThreadLocal<org.ebfhub.fastprotobuf.FastProtoWriter> DEFAULT_WRITER=ThreadLocal.withInitial(FastProtoWriter::new);

    protected static FastProtoReader getDefaultReader(){
        return DEFAULT_PARSER.get();
    }
    protected static FastProtoWriter getDefaultWriter(){
        return DEFAULT_WRITER.get();
    }

    protected FastProtoMessageBase(){
    }

    protected FastProtoMessageBase(org.ebfhub.fastprotobuf.FastProtoObjectPool pool){
        this.pool=pool;
    }
    protected static FastProtoObjectPool getDefaultPool(){
        return getDefaultReader().getPool();
    }
    public boolean isSet(org.ebfhub.fastprotobuf.FastProtoField f){
        return (fieldsSet & f.bit)!=0;
    }

    @Override
    public void writeTo(CodedOutputStream codedOutputStream) throws IOException {

        FastProtoWriter writer = getDefaultWriter();
        write(codedOutputStream, writer);
    }
    public static int computeStringSizeNoTag(CharSequence value) {
        int length = Utf8.encodedLength(value);
        return CodedOutputStream.computeUInt32SizeNoTag(length) + length;
    }

    public static int computeStringSize(int fieldNumber, CharSequence value){
        return CodedOutputStream.computeTagSize(fieldNumber)+computeStringSizeNoTag(value);
    }
    public static int computeMessageSize(int fieldNumber, FastProtoMessageBase<?> value){
        return CodedOutputStream.computeTagSize(fieldNumber)+value.getSerializedSize();
    }
    public org.ebfhub.fastprotobuf.FastProtoObjectPool getPool(){
        return this.pool;
    }

    @Override
    public abstract int getSerializedSize();

    public void release(){
        pool.returnOne(this);
    }

    @Override
    public Parser<? extends com.google.protobuf.Message> getParserForType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteString toByteString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] toByteArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        getDefaultWriter().write(outputStream,this);
    }

    @Override
    public void writeDelimitedTo(OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Builder newBuilderForType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Builder toBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override
    public com.google.protobuf.Message getDefaultInstanceForType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> findInitializationErrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInitializationErrorString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Descriptors.Descriptor getDescriptorForType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasOneof(Descriptors.OneofDescriptor oneofDescriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneofDescriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasField(Descriptors.FieldDescriptor fieldDescriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getField(Descriptors.FieldDescriptor fieldDescriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor fieldDescriptor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getRepeatedField(Descriptors.FieldDescriptor fieldDescriptor, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnknownFieldSet getUnknownFields() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInitialized() {
        return true;
    }
}
