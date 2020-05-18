package org.ebfhub.fastprotobuf.sample;

import com.github.ebfhub.fastprotobuf.sample.proto.MarketDataServiceFastGrpc;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.ebfhub.fastprotobuf.FastProtoObjectPool;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class GrpcClient {
    private final MarketDataServiceFastGrpc.MarketDataServiceBlockingStub blockingStub;
    private final MarketDataServiceFastGrpc.MarketDataServiceStub asyncStub;

    public static void main(String[] args) throws InterruptedException {


        String target = "localhost:8980";

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        try {
            GrpcClient client = new GrpcClient(channel);

            FastProtoObjectPool pool=new FastProtoObjectPool();
            SampleMessageFast.DataMessage request  = SampleMessageFast.DataMessage.create(pool);
            client.asyncStub.subscribeToMarketData(request, new StreamObserver<SampleMessageFast.DataMessage>() {

                int updates=0;
                long lastLogged;

                @Override
                public void onNext(SampleMessageFast.DataMessage dataMessage) {
                    //System.out.println("message: "+dataMessage);
                    updates++;
                    long now = System.currentTimeMillis();
                    if(now-lastLogged>2000){
                        lastLogged=now;
                        System.out.println(new Date()+": "+updates+": message: "+dataMessage);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("error");
                    throwable.printStackTrace();

                }

                @Override
                public void onCompleted() {
                    System.out.println("complete");
                    //channel.shutdownNow();

                }
            });
            Thread.currentThread().join();
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.MINUTES);
        }

    }

    /** Construct client for accessing RouteGuide server using the existing channel. */
    public GrpcClient(ManagedChannel channel) {
        blockingStub = MarketDataServiceFastGrpc.newBlockingStub(channel);
        asyncStub = MarketDataServiceFastGrpc.newStub(channel);
    }
}
