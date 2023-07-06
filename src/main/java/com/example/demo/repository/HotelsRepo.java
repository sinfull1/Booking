package com.example.demo.repository;

import com.example.demo.entity.Hotel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelsRepo extends CrudRepository<Hotel, UUID> {

    @Query("SELECT h FROM Hotel h JOIN FETCH h.rooms WHERE h.hotel_id = :id")
    Optional<Hotel> findByIdWithRooms(UUID id);
}


