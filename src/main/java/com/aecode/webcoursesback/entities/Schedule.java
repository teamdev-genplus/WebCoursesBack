package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @Column(nullable = false)
    private String scheduleName;

    /** NUEVO: campos estructurados para crear eventos de calendario desde el front */
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime; // hora local del timezone indicado

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;   // hora local del timezone indicado

    @Column(length = 64)
    private String timezone;             // p.ej. "America/Lima"


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
}
