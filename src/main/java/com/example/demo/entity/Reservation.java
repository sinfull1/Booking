package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "reservations")
public class Reservation extends BaseTemporalEntity{
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "checkinDate")
    private Integer checkinDate;

    @Column(name = "checkoutDate")
    private Integer checkoutDate;

    @Column(name = "status")
    private String status;
    @Column(name = "roomId")
    private String roomId;

    @Column(name = "number")
    private Integer number;

    @Column(name = "total")
    private Integer total;
    @ManyToOne
    @JoinColumn(name = "guestId", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "guestId")
    @JsonIdentityReference(alwaysAsId = true)
    private Guest guest;
    // constructors, getters, and setters omitted for brevity

    public static Reservation create(ReservationDto reservationDto, Guest guest) {
        Reservation reservation = new Reservation();
        reservation.setStatus(reservationDto.getStatus());
        reservation.setGuest(guest);
        reservation.setNumber(reservationDto.getNumber());
        reservation.setCheckinDate(reservationDto.getCheckinDate());
        reservation.setCheckoutDate(reservationDto.getCheckoutDate());
        return reservation;
    }
}


