package com.example.demo.message;

import com.example.demo.entity.Guest;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationDto;
import com.example.demo.entity.Room;
import com.example.demo.repository.GuestRepo;
import com.example.demo.repository.ReservationRepo;
import com.example.demo.repository.RoomRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

@Component
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private CountDownLatch latch = new CountDownLatch(1);
    private String payload;
    @Autowired
    ReservationRepo reservationRepo;
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    GuestRepo guestRepo;
    @Autowired
    RoomRepo roomRepo;

    @KafkaListener(topics = "${test.topic}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) throws IOException {
        LOGGER.info("received payload='{}'", consumerRecord.toString());
        ReservationDto payload = objectMapper.readValue((byte[]) consumerRecord.value(), ReservationDto.class);
        createReservation(payload);
        latch.countDown();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    Reservation createReservation(ReservationDto res) {
        Room room = roomRepo.findById(UUID.fromString(res.getRoomId())).orElse(null);
        Guest guest = guestRepo.findById(UUID.fromString(res.getGuest())).orElse(null);
        Reservation reservation;
        if (room != null && guest != null) {
            Integer num = room.getNumber();
            if (num - res.getNumber() >= 0) {
                room.setNumber(num - res.getNumber());
                roomRepo.save(room);
                reservation = Reservation.create(res, guest);
                reservationRepo.save(Reservation.create(res, guest));
            } else {
                throw new RuntimeException("No Rooms LEft");
            }
        } else {
            throw new RuntimeException("bad room id ");
        }
        return reservation;
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getPayload() {
        return payload;
    }

    // other getters
}