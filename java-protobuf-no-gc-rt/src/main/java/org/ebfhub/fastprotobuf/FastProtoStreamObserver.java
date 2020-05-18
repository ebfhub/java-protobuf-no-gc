package org.ebfhub.fastprotobuf;

public class FastProtoStreamObserver<T extends FastProtoMessageBase<?>> implements io.grpc.stub.StreamObserver<T>
{
    io.grpc.stub.StreamObserver<T> wrapped;

    public FastProtoStreamObserver(io.grpc.stub.StreamObserver<T> wrapped){
        this.wrapped=wrapped;
    }

    @Override
    public void onNext(T t) {
        try
        {
            wrapped.onNext(t);
        }
        finally
        {
            t.release();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        wrapped.onError(throwable);
    }

    @Override
    public void onCompleted() {
        wrapped.onCompleted();
    }
}
