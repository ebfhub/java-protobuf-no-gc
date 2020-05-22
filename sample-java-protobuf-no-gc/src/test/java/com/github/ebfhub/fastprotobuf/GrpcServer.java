package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.MarketDataServiceFastGrpc;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcServer {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());

    private final int port;
    private final Server server;

    private static class MDService extends MarketDataServiceFastGrpc.MarketDataServiceImplBase {
        long lastLogged;

        @Override
        public void subscribeToMarketData(SampleMessageFast.QueryMessage request, StreamObserver<SampleMessageFast.DataMessage> responseObserver) {
            int n=0;
            while(true) {
                SampleMessageFast.DataMessage msg = SampleMessageFast.DataMessage.newBuilder().setSymbol("test").setSymbolId(n).build();
                long now = System.currentTimeMillis();
                if(now-lastLogged>4000){
                    lastLogged=now;
                    System.out.println(new Date()+": "+n+": message: "+msg);
                }
                responseObserver.onNext(msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                n++;
                msg.release();
            }
        }
    }
    public GrpcServer(int port)  {
        this.port=port;
        server = ServerBuilder.forPort(port).addService(new MDService()).build();
    }

    /** Start serving requests. */
    public void start() throws IOException {
        server.start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    GrpcServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    /** Stop serving requests and shutdown resources. */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main method.  This comment makes the linter happy.
     */
    public static void main(String[] args) throws Exception {
        GrpcServer server = new GrpcServer(8980);
        server.start();
        server.blockUntilShutdown();
    }

}
