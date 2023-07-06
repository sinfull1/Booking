package com.example.demo;


import com.example.demo.entity.*;
import com.example.demo.repository.GuestRepo;
import com.example.demo.repository.HotelsRepo;
import com.example.demo.repository.ReservationRepo;
import com.example.demo.repository.RoomRepo;
import com.example.demo.utils.DateConvertor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ComponentScan({"org.springframework.cloud.stream.binder.test"})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")

public class TestEntity {
    @Autowired
    private HotelsRepo hotelRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private ReservationRepo reservationRepo;

    @Autowired
    private GuestRepo guestRepo;
    Hotel hotel = new Hotel();

    @BeforeAll
    public void init() {
        hotel.setName("Test Hotel");
        hotel.setAddress("Test Address");
        hotelRepo.save(hotel);
    }
    @Test
    public void testHotelRepo() {
        // Create a hotel06121987n
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setAddress("Test Address");
        hotelRepo.save(hotel);
        Hotel retrievedHotel = hotelRepo.findById(hotel.getHotel_id()).orElse(null);
        assertEquals(hotel, retrievedHotel);
    }

    @Test
    //https://vladmihalcea.com/an-entity-modeling-strategy-for-scaling-optimistic-locking/
    public void versionLessOptimistic()  {
        try {
            List<Future> result = new ArrayList<>();
            Random random = new Random();
            ExecutorCompletionService exec = new ExecutorCompletionService(Executors.newFixedThreadPool(3));
            for (int i = 0; i < 3; i++) {
                result.add(exec.submit(() -> {
                    final Hotel retrievedHotel = hotelRepo.findById(hotel.getHotel_id()).orElse(null);
                    String address = "sedfjghb" + random.nextInt(394587);
                    if (retrievedHotel != null) {
                        retrievedHotel.setAddress(address);
                        System.out.println("Saving now" + address);
                        hotelRepo.save(retrievedHotel);
                    }
                    return address;
                }));
            }
            for (int i = 0; i < 3; i++) {
                result.get(i).get();
            }
        }
        catch (Exception ex) {
            //org.springframework.orm.ObjectOptimisticLockingFailureException:
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testRoomRepo() {
        // Create a hotel
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setAddress("Test Address");
        Room room = new Room();
        room.setNumber(Integer.valueOf("101"));
        room.setType(RoomType.SINGLE);
        room.setPrice(100);
        hotel.setRooms(List.of(room));
        hotelRepo.save(hotel);
        // Retrieve the room by ID
        Room retrievedRoom = roomRepo.findById(room.getRoom_id()).orElse(null);
        assertEquals(room, retrievedRoom);
        Hotel retreivedHotel = hotelRepo.findById(hotel.getHotel_id()).orElse(null);
        List<Room> rooms = retreivedHotel.getRooms();
        rooms.get(0).setPrice(123);
        roomRepo.save(room);
        retreivedHotel = hotelRepo.findById(hotel.getHotel_id()).orElse(null);
        System.out.println(retreivedHotel.getRooms().get(0).getPrice());
    }

    @Test
    public void testReservationRepo() {
        // Create a hotel
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setAddress("Test Address");
        hotelRepo.save(hotel);

        // Create a room
        Room room = new Room();
        room.setNumber(Integer.valueOf("101"));
        room.setType(RoomType.SINGLE);
        room.setPrice(100);
        room.setHotel(hotel);
        roomRepo.save(room);

        // Create a guest
        Guest guest = new Guest();
        guest.setName("Test Guest");
        guest.setEmail("test@example.com");
        guest.setPhone("+1 123-456-7890");
        guestRepo.save(guest);

        // Create a reservation
        Reservation reservation = new Reservation();
        reservation.setCheckinDate(DateConvertor.getToday());
        reservation.setCheckoutDate(DateConvertor.getToday());
        reservation.setRoomId(room.getRoom_id().toString());
        reservation.setGuest(guest);
        reservationRepo.save(reservation);

        // Retrieve the reservation by ID
        Reservation retrievedReservation = reservationRepo.findById(reservation.getId()).orElse(null);
        assertEquals(reservation, retrievedReservation);
    }

    @Test
    public void testGuestRepo() {
        // Create a guest
        Guest guest = new Guest();
        guest.setName("Test Guest");
        guest.setEmail("test@example.com");
        guest.setPhone("+1 123-456-7890");
        guestRepo.save(guest);

        // Retrieve the guest by ID
        Guest retrievedGuest = guestRepo.findById(guest.getGuestId()).orElse(null);
        assertEquals(guest, retrievedGuest);
    }
}
