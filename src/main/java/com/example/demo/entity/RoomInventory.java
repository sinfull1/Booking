package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Data;
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
@Table(name = "inventory")
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate
public class RoomInventory {

    @Id
    @GeneratedValue(generator = "uuid-hash")
    @GenericGenerator(name = "uuid-hash", strategy = "com.example.demo.entity.UUIDHashGenerator")

    private UUID inventory;

    @Column(name = "roomId")
    private String roomId;

    @Column(name = "date")
    private Integer date;

    @Column(name = "roomLeft")
    private int roomLeft;
}
