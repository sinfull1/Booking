package com.example.demo.repository;

import com.example.demo.entity.Hotel;
import com.example.demo.entity.RoomInventory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface RoomInvRepo extends CrudRepository<RoomInventory, UUID> {

    @Query("SELECT h FROM RoomInventory h WHERE h.roomId = :id and h.date>=:startDate and h.date <= :endDate")
    List<RoomInventory> findByRoomIdInDateRange(String id, Integer startDate, Integer endDate);
}
