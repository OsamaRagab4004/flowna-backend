package com.flowna.app.studySquad.RoomMessage;


                    import com.flowna.app.studySquad.Room.Room;
                    import com.flowna.app.studySquad.Room.RoomRepository;
                    import com.flowna.app.user.User;
                    import com.flowna.app.user.UserRepository;
                    import lombok.RequiredArgsConstructor;
                    import org.springframework.stereotype.Service;
                    import org.springframework.transaction.annotation.Transactional;

                    import java.util.List;
                    import java.util.stream.Collectors;

                    @Service
                    @RequiredArgsConstructor
                    public class RoomMessageService {

                        private final RoomMessageRepository roomMessageRepository;
                        private final RoomRepository roomRepository;
                        private final UserRepository userRepository;


                        // get all messages with details of user
                        @Transactional(readOnly = true)
                        public List<RoomMessageDTO> getAllMessagesWithUserDetails(String roomJoinCode) {
                            Room room = roomRepository.findByRoomJoinCode(roomJoinCode).orElseThrow(
                                    () -> new IllegalArgumentException("Room not found with join code: " + roomJoinCode)
                            );
                            List<RoomMessage> messages = roomMessageRepository.findAllByRoom(room);
                            return messages.stream()
                                    .map(message -> RoomMessageDTO.builder()
                                            .id(message.getId())
                                            .msgContent(message.getMsgContent())
                                            .createdAt(message.getCreatedAt())
                                            .username(message.getUser() != null ? message.getUser().getUniqueName() : "Unknown User")
                                            .build())
                                    .collect(Collectors.toList());
                        }





                        public RoomMessage saveMessage(RoomMessageRequest request, int userId) {
                            // Validate the room exists
                            Room room = roomRepository.findByRoomJoinCode(request.getRoomJoinCode()).orElseThrow(
                                    () -> new IllegalArgumentException("Room not found with join code: " + request.getRoomJoinCode())
                            );
                            User user = userRepository.findById(userId).orElseThrow(
                                        () -> new IllegalArgumentException("User not found with ID: " + userId)
                                );

                                // Create a new RoomMessage entity
                                RoomMessage roomMessage = RoomMessage.builder()
                                        .msgContent(request.getMsgContent())
                                        .room(room)
                                        .user(user)
                                        .build();

                                // Save the message to the repository
                                return roomMessageRepository.save(roomMessage);
                        }










                    }