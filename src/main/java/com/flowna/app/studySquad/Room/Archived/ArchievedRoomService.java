package com.flowna.app.studySquad.Room.Archived;

import com.flowna.app.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArchievedRoomService {

    private final ArchievedRoomRepository archievedRoomRepository;

    /**
     * Adds an archived room for a user if a room with the same join code does not already exist for that user.
     *
     * @param user The user for whom the archived room is being added.
     * @param roomToArchive The room details to be archived.
     * @return The newly created ArchievedRoom, or the existing one if found.
     */
    public ArchievedRoom addArchivedRoomForUser(User user, ArchievedRoom roomToArchive) {
        // Check if an archived room with the same join code already exists for the user.
        Optional<ArchievedRoom> existingArchivedRoom = archievedRoomRepository.findByRoomJoinCodeAndUser(roomToArchive.getRoomJoinCode(), user);

        if (existingArchivedRoom.isPresent()) {
            // If it exists, return the existing room without creating a new one.
            return existingArchivedRoom.get();
        } else {
            // If it does not exist, associate the room with the user and save it.
            roomToArchive.setUser(user);
            return archievedRoomRepository.save(roomToArchive);
        }
    }



    /**
     * Retrieves a list of all archived rooms for a specific user.
     *
     * @param user The user whose archived rooms are to be retrieved.
     * @return A list of ArchievedRoom objects.
     */
    public List<ArchievedRoom> getAllArchivedRoomsForUser(User user) {
        return archievedRoomRepository.findAllByUser(user);
    }


}