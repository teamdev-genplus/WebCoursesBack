package com.aecode.webcoursesback.dtos;

public class FreqQuestDTO {
    private int freqquestId;
    private String questionText;
    private String answerText;

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
