package com.flowna.app.studySquad.Room;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowna.app.studySquad.QuestionsCollector.QuestionsCollector;
import com.flowna.app.studySquad.RoomLecture.RoomLecture;
import com.flowna.app.studySquad.RoomMessage.RoomMessage;
import com.flowna.app.studySquad.RoomStudentScore.RoomStudentScore;
import com.flowna.app.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import com.flowna.app.studySquad.RoomGoal.RoomGaol;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room")
@Schema(description = "rooms which user can join in study squad feature")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Schema(description = "Name of the room", example = "Room 1")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Description of the room", example = "This is a room for studying together")
    @Column(nullable = false, unique = true)
    private String roomJoinCode;

    @Schema(description = "Define room status if it's online or it's archived")
    @Column(nullable = false)
    private Boolean isOnline = true;

    @Column(nullable = false)
    private int roomHoursGoal;

    @Column(nullable = false)
    private int studyMinutes;

    @Column(nullable = false)
    private int learningMinutes;

    @Column
    private String discordLink;




    // Many-to-One relation for the host
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User host;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "room",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<RoomGaol> roomGoal;




    // Many-to-Many relation with User
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "rooms", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private Set<User> users = new HashSet<>();


    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoomMessage> roomMessages = new HashSet<>();


    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RoomStudentScore> roomStudentScores = new HashSet<>();


    @OneToMany
    (mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    Set<RoomLecture> roomLectures = new HashSet<>();

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<QuestionsCollector> questionsCollectors = new HashSet<>();











}