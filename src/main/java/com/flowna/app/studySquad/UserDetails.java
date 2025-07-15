package com.flowna.app.studySquad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    @Builder.Default
    private String id = java.util.UUID.randomUUID().toString();
    private String username; // The username of the user
    private boolean host;    // The email address of the user
    private boolean ready;
    private boolean studying; // Indicates if the user is currently studying
    private int score;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetails that = (UserDetails) o;
        return Objects.equals(username, that.username); // or use id if available
    }


}
