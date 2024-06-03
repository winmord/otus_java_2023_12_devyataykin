package ru.otus.protobuf;

import io.grpc.ServerBuilder;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.service.RealNumbersServiceImpl;
import ru.otus.protobuf.service.RemoteNumbersServiceImpl;

public class NumbersServer {
    private static final Logger logger = LoggerFactory.getLogger(NumbersServer.class);
    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        var intervalService = new RealNumbersServiceImpl();
        var remoteIntervalService = new RemoteNumbersServiceImpl(intervalService);

        var server = ServerBuilder.forPort(SERVER_PORT)
                .addService(remoteIntervalService)
                .build();
        server.start();
        logger.info("server waiting for client connections...");
        server.awaitTermination();
    }
}
