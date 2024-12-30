package com.example.service;

import com.example.entity.Answer;
import com.example.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class AnswerService {
    @Autowired
    private AnswerRepository answerRepository;


    public Answer createAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Answer getAnswerById(int id) {
        return answerRepository.findById(id).orElse(null);
    }

    public void deleteAnswer(int id) {
        answerRepository.deleteById(id);
    }
}
