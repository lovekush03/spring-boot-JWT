package com.example.jwt.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
        @Column is part of the Jakarta Persistence API (JPA) and is used within entity classes
        to specify details about the column that stores a particular field

        nullable attribute is used to indicate whether column can contain null or not
    */
    @Column(nullable = false, unique = true)
    private String token;

    /*
        name attribute is used to specify exact column name in the DB
     */
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    /*
        updatable attribute determines whether column is included in SQL UPDATE Command
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    /*
        optional bidirectional mapping; keep it simple and store userId
        @ManyToOne: This annotation declares a many-to-one relationship.
        It signifies that multiple instances of the current entity can be associated
        with a single instance of the related entity (in this case, a "User").
        For example, many Post entities could be associated with one User entity.

        FetchType.LAZY: This indicates that the associated "User" entity will only be loaded
        from the database when it is explicitly accessed (e.g., when you call post.getUser()).
        This is the recommended approach for performance optimization, as it
        avoids loading unnecessary data. Opposite is FetchType.Eager

        This annotation defines the foreign key column in the current entity's table
        that links it to the related "User" entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
