package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.message.KafkaProducer;
import com.example.demo.repository.*;
import com.example.demo.utils.DateConvertor;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class HotelRouter {
    @Autowired
    private HotelsRepo hotelRepository;
    @Autowired
    private ReservationRepo reservationRepo;
    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private GuestRepo guestRepo;
    @Autowired
    private RoomInvRepo roomInvRepo;
    @Autowired
    KafkaProducer kafkaProducer;

    @Bean
    public RouterFunction<ServerResponse> hotelRoutes() {
        return route(GET("/hotels"), this::getAllHotels)
                .andRoute(GET("/hotels/{id}"), this::getHotelById)
                .andRoute(GET("/guests/{id}"), this::getGuestById)
                .andRoute(GET("/guests"), this::getAllGuest)
                .andRoute(POST("/hotels"), this::createHotels)
                .andRoute(POST("/api/guest"), this::creatGuest)
                .andRoute(POST("/api/guests"), this::creatGuests)
                .andRoute(DELETE("/hotels/{id}"), this::deleteHotel)
                .andRoute(POST("/{id}/rooms"), this::addRoomToHotel)
                .andRoute(POST("/api/room"), this::createRooms)
                .andRoute(POST("/api/reservations"), this::createReservation)
                .andRoute(POST("/api/payment"), this::makePayment)
                .andRoute(GET("/api/reservations"), this::getAllReservations)
                .andRoute(GET("/api/room"), this::getAllRooms)
                .andRoute(GET("/api/inventory"), this::getInventory);

    }

    private Mono<ServerResponse> getInventory(ServerRequest serverRequest) {
        Iterable<RoomInventory> inventory = roomInvRepo.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Flux.fromIterable(inventory), RoomInventory.class));
    }

    private Mono<ServerResponse> getAllGuest(ServerRequest serverRequest) {
        Iterable<Guest> guest = guestRepo.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Flux.fromIterable(guest), Guest.class));
    }

    private Mono<ServerResponse> creatGuests(ServerRequest serverRequest) {
        Mono<Guest[]> guestMono = serverRequest.bodyToMono(Guest[].class);
        return guestMono.flatMap(guest -> {
            guestRepo.saveAll(Arrays.asList(guest));
            return ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Arrays.asList(guest)));
        });
    }

    private Mono<ServerResponse> createHotels(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Hotel[].class).flatMap(hotel -> {
            hotelRepository.saveAll(Arrays.asList(hotel));
            return ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(Arrays.asList(hotel)));
        });
    }

    private Mono<ServerResponse> getAllRooms(ServerRequest serverRequest) {
        Iterable<Room> rooms = roomRepo.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Flux.fromIterable(rooms), Room.class));
    }

    private Mono<ServerResponse> getAllReservations(ServerRequest serverRequest) {
        Iterable<Reservation> reservations = reservationRepo.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Flux.fromIterable(reservations), Reservation.class));
    }

    private Mono<ServerResponse> createRooms(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Room[].class).flatMap(room ->
        {
            roomRepo.saveAll(Arrays.asList(room));
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(room));
        });
    }

    @Transactional
    Mono<ServerResponse> makePayment(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Payment.class).flatMap(payment -> {
            Reservation monores = reservationRepo.findById(UUID.fromString(payment.getResId())).orElse(null);
            List<RoomInventory> room = roomInvRepo.findByRoomIdInDateRange(monores.getRoomId().toString(), monores.getCheckinDate(), monores.getCheckoutDate());
            if (monores != null && !"PAID".equals(monores.getStatus())) {
                if (!room.stream().map(x-> x.getRoomLeft()> monores.getNumber()).collect(Collectors.toList()).contains(false)){
                    if (monores.getGuest().getBalance() > monores.getTotal() ) {
                        monores.getGuest().setBalance(monores.getGuest().getBalance() - monores.getTotal());
                        guestRepo.save(monores.getGuest());
                        for (RoomInventory inv: room){
                            inv.setRoomLeft(inv.getRoomLeft()-monores.getNumber());
                        }
                        roomInvRepo.saveAll(room);
                        monores.setStatus("PAID");
                        reservationRepo.save(monores);
                    }
                };

            }
            return ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(monores));

        });
    }

    private Mono<ServerResponse> getGuestById(ServerRequest serverRequest) {
        UUID id = UUID.fromString(serverRequest.pathVariable("id"));
        Optional<Guest> guest = guestRepo.findById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(guest.get()));
    }


    @Bean
    public RouterFunction<ServerResponse> hotelRoutesAsync() {
        return route(POST("/a/api/reservations"), this::createReservationAsync);
    }


    Mono<ServerResponse> createReservationAsync(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Reservation.class).flatMap(reservation -> {
            try {
                kafkaProducer.send("test-topic", reservation);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(reservation);
        }).flatMap(guest -> ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("Request Queued")));
    }

    @Transactional
    Mono<ServerResponse> creatGuest(ServerRequest serverRequest) {
        Mono<Guest> guestMono = serverRequest.bodyToMono(Guest.class);
        return guestMono.flatMap(guest -> {
            guestRepo.save(guest);
            return ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(guest));
        });
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<ServerResponse> createReservation(ServerRequest request) {
        Mono<Reservation> reservationMono = request.bodyToMono(Reservation.class);
        return reservationMono.flatMap(res -> {
            List<RoomInventory> roomInv =
                    roomInvRepo.findByRoomIdInDateRange(res.getRoomId(), res.getCheckinDate(), res.getCheckoutDate());
            Guest guest = guestRepo.findById(res.getGuest().getGuestId()).orElse(null);
            Room room = roomRepo.findById(UUID.fromString(res.getRoomId())).orElse(null);
            if (roomInv.stream().map(x-> x.getRoomLeft()>= res.getNumber()).collect(Collectors.toList()).contains(false)){
                return ServerResponse.badRequest().body(BodyInserters.fromValue("Booking Not possible"));
            };
            if (roomInv != null && guest != null) {
                if (room.getPrice() * res.getNumber() <= guest.getBalance()) {
                    res.setStatus("BLOCKED");
                    res.setTotal(room.getPrice() * res.getNumber());
                    reservationRepo.save(res);
                } else {
                    return ServerResponse.badRequest().body(BodyInserters.fromValue("No Rooms LEft"));
                }
            } else {
                return ServerResponse.badRequest().body(BodyInserters.fromValue("Bad room or guest"));
            }
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(BodyInserters.fromValue(res)));
        });
    }

    @Transactional
    public Mono<ServerResponse> getAllHotels(ServerRequest request) {
        Iterable<Hotel> hotels = hotelRepository.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Flux.fromIterable(hotels), Hotel.class));
    }

    @Transactional
    public Mono<ServerResponse> getHotelById(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));
        Optional<Hotel> hotel = hotelRepository.findById(id);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(hotel.get()));

    }


    private Mono<ServerResponse> deleteHotel(ServerRequest request) {
        UUID id = UUID.fromString(request.pathVariable("id"));
        return Mono.just(hotelRepository.findById(id)).flatMap(hotel -> {
                    if (hotel == null) {
                        return ServerResponse.notFound().build();
                    } else {
                        hotelRepository.delete(hotel.get());
                        return ServerResponse.noContent().build();
                    }
                }
        );
    }

    @Transactional
    Mono<ServerResponse> addRoomToHotel(ServerRequest request) {
        UUID hotelId = UUID.fromString(request.pathVariable("id"));
        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);

        return request.bodyToMono(Room[].class)
                .flatMap(rooms -> {
                    Arrays.asList(rooms).forEach(room -> {
                        room.setHotel(hotel);
                        roomRepo.save(room);
                        // save entry for all dates
                        roomInvRepo.saveAll(DateConvertor.getListToday().stream().map(date ->{
                            RoomInventory roomInventory = new RoomInventory();
                            roomInventory.setRoomId(room.getRoom_id().toString());
                            roomInventory.setRoomLeft(room.getNumber());
                            roomInventory.setDate(date);
                            return roomInventory;
                        }).collect(Collectors.toList()));

                    });
                    return Mono.empty();
                }).flatMap(x ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(hotel)));

    }


}
