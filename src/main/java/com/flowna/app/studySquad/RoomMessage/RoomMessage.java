package com.flowna.app.studySquad.RoomMessage;


import com.flowna.app.studySquad.Room.Room;
import com.flowna.app.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
public class RoomMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

@Column(nullable = false, columnDefinition = "TEXT")
private String msgContent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    User user;


    @PrePersist
    public void onCreate() {
    this.createdAt = LocalDateTime.now();
    }
















}
