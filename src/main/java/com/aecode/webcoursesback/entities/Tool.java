package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tools")
@SequenceGenerator(name = "tool_seq", sequenceName = "tool_sequence", allocationSize = 1)
public class Tool{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tool_seq")
    private int toolId;

    @ManyToMany(mappedBy = "tools")
    private List<SecondaryCourses> secondary_courses;

    @ManyToMany(mappedBy = "tools")
    private List<Course> courses;

    @Column(length = 255)
    private String name;

    @Column( length = 255)
    private String picture;

    public Tool() {
    }

    public Tool(int toolId, List<SecondaryCourses> secondary_courses, List<Course> courses, String name, String picture) {
        this.toolId = toolId;
        this.secondary_courses = secondary_courses;
        this.courses = courses;
        this.name = name;
        this.picture = picture;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public int getToolId() {
        return toolId;
    }

    public void setToolId(int toolId) {
        this.toolId = toolId;
    }

    public List<SecondaryCourses> getSecondary_courses() {
        return secondary_courses;
    }

    public void setSecondary_courses(List<SecondaryCourses> secondary_courses) {
        this.secondary_courses = secondary_courses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
