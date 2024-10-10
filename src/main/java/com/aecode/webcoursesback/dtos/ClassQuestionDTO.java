package com.aecode.webcoursesback.dtos;
import java.util.Set;

public class ClassQuestionDTO {
    private int questionId;
    private int classId;
    private String questionText;
    private Set<ClassAnswerDTO> classAnswers;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Set<ClassAnswerDTO> getClassanswers() {
        return classAnswers;
    }

    public void setClassanswers(Set<ClassAnswerDTO> classAnswers) {
        this.classAnswers = classAnswers;
    }
}
