package com.example.demo.entity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UUIDHashGenerator extends UUIDGenerator {
    private static final String ALGORITHM = "SHA-256";

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        return switch (object) {
            case Hotel hotel ->  createHash(hotel.getName());
            case Room room -> createHash(room.getNumber() + room.getType().toString() + room.getHotel().getHotel_id().toString());
            case Guest guest -> createHash (guest.getName() + guest.getEmail() + guest.getPhone());
            case Reservation reservation -> createHash(reservation.getRoomId() + reservation.getGuest().getGuestId().toString());
            case RoomInventory roomInventory -> createHash(roomInventory.getRoomId() + roomInventory.getDate());
            default -> (Serializable) super.generate(session, object);
        };

    }

    private Serializable createHash(String data) {
        if (StringUtils.hasText(data)) {
            try {
                MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
                byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
                ByteBuffer buffer = ByteBuffer.wrap(hash);
                return new UUID(buffer.getLong(), buffer.getLong());
            } catch (NoSuchAlgorithmException e) {
                throw new HibernateException("Failed to generate UUID", e);
            }
        }
        return "";
    }
}
