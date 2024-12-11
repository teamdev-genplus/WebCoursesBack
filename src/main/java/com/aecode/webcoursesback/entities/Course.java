package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "courses")
@SequenceGenerator(name = "course_seq", sequenceName = "course_sequence", allocationSize = 1)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_seq")
    private int courseId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column( length = 255)
    private String coverimage;

    @Column( length = 255)
    private String videoUrl;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();


    public Course() {
    }

    public Course(int courseId, String title, String coverimage, String videoUrl, List<Module> modules) {
        this.courseId = courseId;
        this.title = title;
        this.coverimage = coverimage;
        this.videoUrl = videoUrl;
        this.modules = modules;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(String coverimage) {
        this.coverimage = coverimage;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
