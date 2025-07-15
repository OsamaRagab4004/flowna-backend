package com.flowna.app.studySquad.Room;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingUser {
    private String username;
    private boolean typing;
    private String roomJoinCode;

}
