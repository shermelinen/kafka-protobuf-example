package se.tink.engine;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import se.tink.events.EventEnvelope;
import se.tink.events.payments.PaymentInitialized;

@Component
@EnableBinding(Sink.class)
public class Consumer {
    static final String PROTO_PACKAGE = "se.tink.events.payments";
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @StreamListener(Sink.INPUT)
    public void process(Message<?> message) throws InvalidProtocolBufferException {
        EventEnvelope event = EventEnvelope.parseFrom(ByteString.copyFromUtf8((String) message.getPayload()));

        PaymentInitialized paymentInitialized = event.getData().unpack(PaymentInitialized.class);

        logger.info("process = Received traceId " + event + ". Payment = " + paymentInitialized.toString());
    }

    @StreamListener(Sink.INPUT)
    public void processGeneric(Message<?> message) throws InvalidProtocolBufferException, ClassNotFoundException {

        EventEnvelope event = EventEnvelope.parseFrom(ByteString.copyFromUtf8((String) message.getPayload()));
        Class messageClass = Class.forName(event.getEventName()).asSubclass(com.google.protobuf.Message.class);
        com.google.protobuf.Message data = (com.google.protobuf.Message) event.getData().unpack(messageClass);

        logger.info("processGeneric = Received traceId " + event + ". Payment = " + data.toString());
    }
}
