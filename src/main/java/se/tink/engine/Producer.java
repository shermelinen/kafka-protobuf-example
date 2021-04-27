package se.tink.engine;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import se.tink.events.EventEnvelope;
import se.tink.events.payments.PaymentInitialized;

@Service
@EnableBinding(Source.class)
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    @Qualifier("output")
    private MessageChannel output;

    public void sendMessage() throws InterruptedException, InvalidProtocolBufferException {
        int paymentId = 0;
        int eventId = 1000;

        while (true) {

            PaymentInitialized payment = PaymentInitialized.newBuilder()
                    .setCustomerId("1")
                    .setAmount(100)
                    .setPaymentId(paymentId++).build();

            eventId++;
            EventEnvelope event = EventEnvelope.newBuilder()
                    .setTraceId(String.valueOf(eventId))
                    .setEventName(PaymentInitialized.class.getName())
                    .setData(
                            Any.pack(payment))
                    .build();

            output.send(
                    MessageBuilder.withPayload(event.toByteString().toStringUtf8()).build());
            logger.info("Sending event");
            Thread.sleep(5000);
        }

    }
}
