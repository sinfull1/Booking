package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "rooms")
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class Room  extends BaseTemporalEntity{
    @Id
    @GeneratedValue(generator = "uuid-hash")
    @GenericGenerator(name = "uuid-hash", strategy = "com.example.demo.entity.UUIDHashGenerator")

    private UUID room_id;

    @Column(name = "number")
    private Integer number;
    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false, updatable = false)
    @JsonBackReference
    private Hotel hotel;
    @Column(name = "type")
    private RoomType type;

    @Column(name = "price")
    private int price;



}