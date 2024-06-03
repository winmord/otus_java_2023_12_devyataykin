package ru.otus.protobuf;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"squid:S106", "squid:S2142"})
public class NumbersClient {
    private static final Logger logger = LoggerFactory.getLogger(NumbersClient.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static final AtomicLong lastServerNumber = new AtomicLong(0L);

    public static void main(String[] args) throws InterruptedException {
        logger.info("numbers Client is starting...");
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var clientMessage =
                ClientMessage.newBuilder().setFirstValue(0).setLastValue(30).build();

        var latch = new CountDownLatch(1);
        var newStub = IntervalServiceGrpc.newStub(channel);
        newStub.sendInterval(clientMessage, new StreamObserver<ServerMessage>() {
            private static final Logger observerLogger = LoggerFactory.getLogger(StreamObserver.class);

            @Override
            public void onNext(ServerMessage serverMessage) {
                lastServerNumber.set(serverMessage.getValue());
                observerLogger.info("new value:{}", lastServerNumber.get());
            }

            @Override
            public void onError(Throwable throwable) {
                observerLogger.error(Arrays.toString(throwable.getStackTrace()));
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        clientCounterStart();

        latch.await();
        channel.shutdown();
    }

    private static void clientCounterStart() {
        long result = 0;
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }

            result = result + 1 + lastServerNumber.getAndSet(0);
            logger.info("currentValue:{}", result);
        }
    }
}
