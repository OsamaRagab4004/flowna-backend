package com.flowna.app.studySquad.Room.Archived;

import com.flowna.app.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/squadgames/rooms/archived")
@RequiredArgsConstructor
public class ArchievedRoomController {

    private final ArchievedRoomService archievedRoomService;

    /**
     * Endpoint to add an archived room for the authenticated user.
     *
     * @param roomToArchive The archived room details from the request body.
     * @param user          The authenticated user.
     * @return ResponseEntity with the created or existing archived room.
     */
    @PostMapping("/add")
    public ResponseEntity<ArchievedRoom> addArchivedRoom(
            @RequestBody ArchievedRoom roomToArchive,
            @AuthenticationPrincipal User user) {
        ArchievedRoom archievedRoom = archievedRoomService.addArchivedRoomForUser(user, roomToArchive);
        return ResponseEntity.ok(archievedRoom);
    }

    /**
     * Endpoint to retrieve all archived rooms for the authenticated user.
     *
     * @param user The authenticated user.
     * @return ResponseEntity with a list of archived rooms.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ArchievedRoom>> getAllArchivedRooms(
            @AuthenticationPrincipal User user) {
        List<ArchievedRoom> archivedRooms = archievedRoomService.getAllArchivedRoomsForUser(user);
        return ResponseEntity.ok(archivedRooms);
    }
}