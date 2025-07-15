package com.flowna.app.studySquad.RoomGoal;


import com.flowna.app.studySquad.Room.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_goal")
public class RoomGaol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private int hours;

    @Column(nullable = false)
    private int minutes;

    @Column(nullable = false)
    private boolean done;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;



}
