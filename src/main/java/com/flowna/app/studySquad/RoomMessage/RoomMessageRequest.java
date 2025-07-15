package com.flowna.app.studySquad.RoomMessage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class RoomMessageRequest {

private String msgContent; // The content of the message
private String roomJoinCode; // The unique code of the room where the message is sent


}