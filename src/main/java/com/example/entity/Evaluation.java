package com.example.entity;

import jakarta.persistence.*;

@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int candidateId;
    private long correctAnswers;
    private long incorrectAnswers;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getCandidateId() {
        return candidateId;
    }
    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }
    public long getCorrectAnswers() {
        return correctAnswers;
    }
    public void setCorrectAnswers(long correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
    public long getIncorrectAnswers() {
        return incorrectAnswers;
    }
    public void setIncorrectAnswers(long incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
}
