package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import java.util.List;
import ru.otus.protobuf.ClientMessage;
import ru.otus.protobuf.IntervalServiceGrpc;
import ru.otus.protobuf.ServerMessage;
import ru.otus.protobuf.model.Interval;
import ru.otus.protobuf.model.Number;

@SuppressWarnings({"squid:S106", "squid:S2142"})
public class RemoteNumbersServiceImpl extends IntervalServiceGrpc.IntervalServiceImplBase {
    private final NumbersService numbersService;

    public RemoteNumbersServiceImpl(NumbersService numbersService) {
        this.numbersService = numbersService;
    }

    @Override
    public void sendInterval(ClientMessage request, StreamObserver<ServerMessage> responseObserver) {
        List<Number> allNumbers =
                numbersService.sendInterval(new Interval(request.getFirstValue(), request.getLastValue()));
        allNumbers.forEach(v -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            responseObserver.onNext(value2ServerMessage(v));
        });
        responseObserver.onCompleted();
    }

    private ServerMessage value2ServerMessage(Number number) {
        return ServerMessage.newBuilder().setValue(number.getValue()).build();
    }
}
