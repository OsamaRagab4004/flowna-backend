package com.flowna.app.studySquad.Room.Archived;

import com.flowna.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchievedRoomRepository extends JpaRepository<ArchievedRoom, Integer> {

    /**
     * Finds an archived room by its join code and the associated user.
     * @param roomJoinCode The join code of the room.
     * @param user The user associated with the room.
     * @return an Optional containing the ArchievedRoom if found, otherwise empty.
     */
    Optional<ArchievedRoom> findByRoomJoinCodeAndUser(String roomJoinCode, User user);



    /**
     * Finds all archived rooms for a given user.
     * @param user The user to find archived rooms for.
     * @return A list of archived rooms for the user.
     */
    List<ArchievedRoom> findAllByUser(User user);



}