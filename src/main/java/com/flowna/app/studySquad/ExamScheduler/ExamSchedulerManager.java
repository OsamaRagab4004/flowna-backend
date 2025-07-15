package com.flowna.app.studySquad.ExamScheduler;

    import org.springframework.stereotype.Component;

    import java.util.Set;
    import java.util.concurrent.ConcurrentHashMap;

    @Component
    public class ExamSchedulerManager {

        private final ConcurrentHashMap<String, Set<String>> examParticipants = new ConcurrentHashMap<>();

        /**
         * Adds a player to an exam for a specific room.
         *
         * @param roomId The ID of the room.
         * @param username The username of the player.
         */
        public void addPlayerToExam(String roomId, String username) {
            examParticipants.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(username);
        }

        /**
         * Removes a player from an exam for a specific room.
         *
         * @param roomId The ID of the room.
         * @param username The username of the player to remove.
         */
        public void removePlayerFromExam(String roomId, String username) {
            Set<String> participants = examParticipants.get(roomId);
            if (participants != null) {
                participants.remove(username);
                if (participants.isEmpty()) {
                    examParticipants.remove(roomId);
                }
            }
        }

        /**
         * Gets the number of participants in an exam for a specific room.
         *
         * @param roomId The ID of the room.
         * @return The number of participants.
         */
        public int getParticipantCount(String roomId) {
            return examParticipants.getOrDefault(roomId, ConcurrentHashMap.newKeySet()).size();
        }






    }