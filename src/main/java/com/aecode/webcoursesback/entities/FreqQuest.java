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
    private List<SecondaryCourses> secondary_courses;

    @Column(length = 255)
    private String answerText;

    public FreqQuest() {
    }

    public FreqQuest(int freqquestId, String questionText, List<SecondaryCourses> secondary_courses, String answerText) {
        this.freqquestId = freqquestId;
        this.questionText = questionText;
        this.secondary_courses = secondary_courses;
        this.answerText = answerText;
    }

    public List<SecondaryCourses> getSecondary_courses() {
        return secondary_courses;
    }

    public void setSecondary_courses(List<SecondaryCourses> secondary_courses) {
        this.secondary_courses = secondary_courses;
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

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
