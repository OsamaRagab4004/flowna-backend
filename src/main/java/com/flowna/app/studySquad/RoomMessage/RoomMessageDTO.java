package com.flowna.app.studySquad.RoomMessage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMessageDTO {
    private Integer id;
    private String msgContent;
    private LocalDateTime createdAt;
    private String username;

}
