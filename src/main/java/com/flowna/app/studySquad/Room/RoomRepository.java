package com.flowna.app.studySquad.Room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    // add findByRoomJoinCode
    Optional<Room> findByRoomJoinCode(String roomJoinCode); // Find a room by its unique join code
}
