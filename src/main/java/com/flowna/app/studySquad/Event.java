package com.flowna.app.studySquad;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private EventType eventType; // e.g., "newUserJoined", "sendMessage"
    private Object payload;   // event-specific data


}