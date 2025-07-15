package com.flowna.app.studySquad.RoomLecture;

import com.flowna.app.studySquad.QuestionCW.QuestionCW;
import com.flowna.app.studySquad.QuestionFillBlank.QuestionFillBlank;
import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
import com.flowna.app.studySquad.Room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="room_lecture")
public class RoomLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String title;


    @Column(columnDefinition = "TEXT")
    private String DefinitionsJson;

    @Column(columnDefinition = "TEXT")
    private String StepbystepJson;

    @Column(columnDefinition = "TEXT")
    private String QAJson;


    @Column(columnDefinition = "TEXT")
    private String MindMapsJson;

    @Column
    LocalDateTime creationTime;



    @PrePersist
    public void prePersist() {
        this.creationTime = LocalDateTime.now();
    }









    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;











}
