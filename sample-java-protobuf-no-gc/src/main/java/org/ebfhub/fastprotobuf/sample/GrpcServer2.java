package org.ebfhub.fastprotobuf.sample;

import com.github.ebfhub.fastprotobuf.sample.proto.MarketDataServiceGrpc;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessage;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GrpcServer2 {
    private static final Logger logger = Logger.getLogger(GrpcServer2.class.getName());

    private final int port;
    private final Server server;

    private static class MDService extends MarketDataServiceGrpc.MarketDataServiceImplBase {
        @Override
        public void subscribeToMarketData(SampleMessage.DataMessage request, StreamObserver<SampleMessage.DataMessage> responseObserver) {
            int n=0;
            while(true) {
                SampleMessage.DataMessage msg = SampleMessage.DataMessage.newBuilder().setSymbol("test").setSymbolId(n).build();
                responseObserver.onNext(msg);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                n++;
                //msg.release();
            }
        }
    }
    public GrpcServer2(int port)  {
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
                    GrpcServer2.this.stop();
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
        GrpcServer2 server = new GrpcServer2(8980);
        server.start();
        server.blockUntilShutdown();
    }

}
