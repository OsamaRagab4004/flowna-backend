package com.flowna.app.studySquad.RoomMessage;


import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomMessageManager {

    final ConcurrentHashMap <String, Set<RoomMessageDTO>> rooms = new ConcurrentHashMap<>();

    public void addMessage(String roomJoinCode, RoomMessageDTO message) {
        // This correctly and atomically adds a message to a room's message set.
        rooms.computeIfAbsent(roomJoinCode, k -> ConcurrentHashMap.newKeySet()).add(message);
    }

    public Set<RoomMessageDTO> getMessages(String roomJoinCode) {
        // This ensures that if a room doesn't exist, a new set is created,
        // stored in the map, and then returned.
        return rooms.computeIfAbsent(roomJoinCode, k -> ConcurrentHashMap.newKeySet());
    }
}