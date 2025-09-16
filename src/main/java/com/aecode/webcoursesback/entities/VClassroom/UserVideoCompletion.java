package com.aecode.webcoursesback.entities.VClassroom;
import com.aecode.webcoursesback.entities.UserProfile;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "user_video_views",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_video_view", columnNames = {"user_profile_id","video_id"}))
@SequenceGenerator(name = "user_video_view_seq", sequenceName = "user_video_view_seq", allocationSize = 1)

public class UserVideoCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_video_view_seq")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_uservideoview_user"))
    private UserProfile userProfile;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false, foreignKey = @ForeignKey(name = "fk_uservideoview_video"))
    private ModuleVideo video;

    /** Marcado por el front cuando terminó de ver el video */
    @Column(name = "completed", nullable = false)
    private boolean completed;
}
