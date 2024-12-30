package com.example.dto;

import com.example.entity.Answer;
import java.util.List;

public class CandidateAnswerRequest {
    private int candidateId;
    private List<Answer> answers;

    // Getters and Setters
    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
