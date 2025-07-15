package com.flowna.app.studySquad.Room.Archived;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowna.app.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "archieved_room")
public class ArchievedRoom {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;


        @Column
        private String name;

        @Column
        private String roomJoinCode;



    // Many-to-One relation for the host
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;







}