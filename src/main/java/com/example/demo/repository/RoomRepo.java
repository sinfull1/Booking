package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepo extends CrudRepository<Room, UUID> {


}