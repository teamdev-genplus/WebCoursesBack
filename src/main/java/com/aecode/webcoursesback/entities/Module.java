package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modules")
@SequenceGenerator(name = "module_seq", sequenceName = "module_sequence", allocationSize = 1)
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "module_seq")
    private int moduleId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column( length = 255)
    private String moduleimage;

    @Column(nullable = false, length = 255)
    private String title;

    @Column( length = 255)
    private String videoUrl;

    @Column()
    private int hours;
    @Column()
    private int percentage;
    @Column()
    private double price;
    @Column()
    private int orderNumber;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Unit> units =  new ArrayList<>();

    @OneToOne(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RelatedWork relatedworks;

    public Module() {
    }

    public Module(int moduleId, Course course, String moduleimage, String title, String videoUrl, int hours, int percentage, double price, int orderNumber, List<Unit> units, RelatedWork relatedworks) {
        this.moduleId = moduleId;
        this.course = course;
        this.moduleimage = moduleimage;
        this.title = title;
        this.videoUrl = videoUrl;
        this.hours = hours;
        this.percentage = percentage;
        this.price = price;
        this.orderNumber = orderNumber;
        this.units = units;
        this.relatedworks = relatedworks;
    }

    public String getModuleimage() {
        return moduleimage;
    }

    public void setModuleimage(String moduleimage) {
        this.moduleimage = moduleimage;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public RelatedWork getRelatedworks() {
        return relatedworks;
    }

    public void setRelatedworks(RelatedWork relatedworks) {
        this.relatedworks = relatedworks;
    }
}
