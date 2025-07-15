package com.flowna.app.studySquad.Room;


import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomTimerManager {

    ConcurrentHashMap<String, RoomTimerSettings> rooms = new ConcurrentHashMap<>();

    // add a new timer for a room
    public void addTimer(String roomJoinCode, RoomTimerSettings timerSettings) {
        rooms.put(roomJoinCode, timerSettings);
    }

    // get the timer for a room
    public RoomTimerSettings getTimer(String roomJoinCode) {
        return rooms.get(roomJoinCode);
    }

    // remove the timer for a room
    public void removeTimer(String roomJoinCode) {
        rooms.remove(roomJoinCode);
    }

    public boolean isTimerEnabled(String roomJoinCode) {
        RoomTimerSettings timerSettings = rooms.get(roomJoinCode);
        return timerSettings != null && timerSettings.isTimerEnabled();
    }







}
