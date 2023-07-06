package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate

public class Guest extends BaseTemporalEntity{
    @Id
    @GeneratedValue(generator = "uuid-hash")
    @GenericGenerator(name = "uuid-hash", strategy = "com.example.demo.entity.UUIDHashGenerator")

    private UUID guestId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    @PartitionKey
    private String phone;
    @Column(name = "balance")
    private int balance;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reservation> reservations;

}