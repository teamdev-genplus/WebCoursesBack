package com.aecode.webcoursesback.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "classquestions")
public class ClassQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionId;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class aclass;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @OneToMany(mappedBy = "classQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassAnswer> classAnswers = new HashSet<>();


    public ClassQuestion() {
    }

    public ClassQuestion(int questionId, Class aclass, String questionText, Set<ClassAnswer> classAnswers) {
        this.questionId = questionId;
        this.aclass = aclass;
        this.questionText = questionText;
        this.classAnswers = classAnswers;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Class getAclass() {
        return aclass;
    }

    public void setAclass(Class aclass) {
        this.aclass = aclass;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Set<ClassAnswer> getClassanswers() {
        return classAnswers;
    }

    public void setClassanswers(Set<ClassAnswer> classanswers) {
        this.classAnswers = classanswers;
    }
}
