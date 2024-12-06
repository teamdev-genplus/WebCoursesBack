package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "freqquests")
@SequenceGenerator(name = "freqquest_seq", sequenceName = "freqquest_sequence", allocationSize = 1)
public class FreqQuest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "freqquest_seq")
    private int freqquestId;

    @Column(length = 255)
    private String questionText;

    @ManyToMany(mappedBy = "freqquests")
    private List<Course> courses;

    @Column(length = 255)
    private String answerText;

    public FreqQuest() {
    }

    public FreqQuest(int freqquestId, String questionText, List<Course> courses, String answerText) {
        this.freqquestId = freqquestId;
        this.questionText = questionText;
        this.courses = courses;
        this.answerText = answerText;
    }

    public int getFreqquestId() {
        return freqquestId;
    }

    public void setFreqquestId(int freqquestId) {
        this.freqquestId = freqquestId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
