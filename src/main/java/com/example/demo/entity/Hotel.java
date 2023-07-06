package com.example.demo.entity;


import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
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
@Table(name= "hotel")

public class Hotel extends BaseTemporalEntity{
    @Id
    @GeneratedValue(generator = "uuid-hash")
    @GenericGenerator(name = "uuid-hash", strategy = "com.example.demo.entity.UUIDHashGenerator")

    private UUID hotel_id;

    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "address")
    @PartitionKey
    private String address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Room> rooms;

}





