package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.MarketDataServiceFastGrpc;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import io.grpc.ConnectivityState;
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


            channel.notifyWhenStateChanged(ConnectivityState.TRANSIENT_FAILURE, ()->{
                System.out.println("State changed");
            });




            setquery(client, ()->{
                subscribeMD(client);

            });


            Thread.currentThread().join();
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.MINUTES);
        }

    }

    private static void setquery(GrpcClient client, Runnable then) {

        SampleMessageFast.QueryMessage msg = SampleMessageFast.QueryMessage.newBuilder()
                .setData(SampleMessageFast.DataMessage.newBuilder().setSymbol("bye"));

        SampleMessageFast.DataMessage res2 = client.blockingStub.setQuery(msg.retain());
        System.out.println("Got1 "+res2);

        client.asyncStub.setQuery(msg, new StreamObserver<SampleMessageFast.DataMessage>() {
            @Override
            public void onNext(SampleMessageFast.DataMessage dataMessage) {
                System.out.println("Got "+dataMessage);

            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();

            }

            @Override
            public void onCompleted() {
                System.out.println("done");


                then.run();
            }
        });
    }

    private static void subscribeMD(GrpcClient client) {
        SampleMessageFast.QueryMessage request  = SampleMessageFast.QueryMessage.newBuilder();

        client.asyncStub.subscribeToMarketData(request, new StreamObserver<SampleMessageFast.StreamMessage>() {

            int updates=0;
            long lastLogged;

            @Override
            public void onNext(SampleMessageFast.StreamMessage dataMessage) {
                //System.out.println("message: "+dataMessage);
                updates++;
                long now = System.currentTimeMillis();
                if(now-lastLogged>4000){
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
    }

    /** Construct client for accessing RouteGuide server using the existing channel. */
    public GrpcClient(ManagedChannel channel) {
        blockingStub = MarketDataServiceFastGrpc.newBlockingStub(channel);
        asyncStub = MarketDataServiceFastGrpc.newStub(channel);
    }
}
