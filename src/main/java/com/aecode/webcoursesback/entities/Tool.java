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
    private List<Course> courses;

    @Column(length = 255)
    private String name;

    @Column( length = 255)
    private String picture;

    public Tool() {
    }

    public Tool(int toolId, List<Course> courses, String name, String picture) {
        this.toolId = toolId;
        this.courses = courses;
        this.name = name;
        this.picture = picture;
    }

    public int getToolId() {
        return toolId;
    }

    public void setToolId(int toolId) {
        this.toolId = toolId;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
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
