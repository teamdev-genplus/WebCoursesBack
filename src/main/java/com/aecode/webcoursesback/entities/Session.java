package com.aecode.webcoursesback.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;

    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String resourceText;

    @Column( length = 255)
    private String resourceDocument;
 
    @Column(nullable = false)
    private int orderNumber;

    @OneToOne(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private SessionTest sessiontests;

    public Session() {
    }

    public Session(int sessionId, Unit unit, String title, String videoUrl, String description, String resourceText, String resourceDocument, int orderNumber, SessionTest sessiontests) {
        this.sessionId = sessionId;
        this.unit = unit;
        this.title = title;
        this.videoUrl = videoUrl;
        this.description = description;
        this.resourceText = resourceText;
        this.resourceDocument = resourceDocument;
        this.orderNumber = orderNumber;
        this.sessiontests = sessiontests;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceText() {
        return resourceText;
    }

    public void setResourceText(String resourceText) {
        this.resourceText = resourceText;
    }

    public String getResourceDocument() {
        return resourceDocument;
    }

    public void setResourceDocument(String resourceDocument) {
        this.resourceDocument = resourceDocument;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public SessionTest getSessiontests() {
        return sessiontests;
    }

    public void setSessiontests(SessionTest sessiontests) {
        this.sessiontests = sessiontests;
    }
}
