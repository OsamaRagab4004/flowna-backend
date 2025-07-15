package com.flowna.app.studySquad.Room;

import com.flowna.app.studySquad.UserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RoomUserManager {

    // Map from roomId to the set of active players in that room
    private final ConcurrentHashMap<String, Set<UserDetails>> roomPlayers = new ConcurrentHashMap<>();


    public void addPlayerToRoom(String roomId, UserDetails user) {
        //check if the user is already in the room
        if (roomPlayers.containsKey(roomId) && roomPlayers.get(roomId).stream().anyMatch(p -> p.getUsername().equals(user.getUsername()))) {
            return; // User already in the room, no need to add again
        }
        roomPlayers.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(user);
    }


  public void removePlayerFromRoom(String roomId, String username) {
      Set<UserDetails> players = roomPlayers.get(roomId);
      if (players != null) {
          players.removeIf(p -> p.getUsername().equals(username) );
          if (players.isEmpty()) {
              roomPlayers.remove(roomId);
          }
      }
  }



  public void toggleUserReady(String roomId, String username) {
      Set<UserDetails> players = roomPlayers.get(roomId);
      if (players != null) {
          players.stream()
                  .filter(p -> p.getUsername().equals(username))
                  .findFirst()
                  .ifPresent(p -> p.setReady(!p.isReady())); // Toggle the ready status
      }
  }
    // Get all players in a room
    public Set<UserDetails> getPlayersInRoom(String roomId) {
        return roomPlayers.getOrDefault(roomId, ConcurrentHashMap.newKeySet());
    }
    public void setPlayerReady(String roomId, UserDetails user, boolean isReady) {
        Set<UserDetails> players = roomPlayers.get(roomId);
        if (players != null) {
            players.stream()
                    .filter(p -> p.getId().equals(user.getId()))
                    .findFirst()
                    .ifPresent(p -> p.setReady(isReady)); // Assuming isHost is used to indicate readiness
        }
    }
 public void setPlayerHost(String roomId, String username, boolean isHost) {
        Set<UserDetails> players = roomPlayers.get(roomId);

     System.out.println("Before :: setPlayerHost::" + players);

        players.forEach(p -> p.setHost(false));


         if (players != null) {
             players.stream()
                     .filter(p -> p.getUsername().equals(username))
                     .findFirst()
                     .ifPresent(p -> p.setHost(isHost));

         }

     System.out.println("After :: setPlayerHost::" + players);
 }














}
