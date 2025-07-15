package com.flowna.app.studySquad.QuestionsCollector;


    import com.flowna.app.studySquad.QuestionMSQ.QuestionMSQ;
    import com.flowna.app.studySquad.Room.Room;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.HashSet;
    import java.util.Set;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "questions_collector")
    public class QuestionsCollector {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(nullable = false)
        private String title;

        @Column
        private LocalDateTime createdAt;



        @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinColumn(name = "room_id", nullable = false)
        private Room room;


        @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @Builder.Default
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        Set<QuestionMSQ> mcqQuestions  = new HashSet<>();




        @PrePersist
        private void prePersist() {
            this.createdAt = LocalDateTime.now();
        }






















    }