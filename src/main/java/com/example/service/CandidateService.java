package com.example.service;

import com.example.entity.Answer;
import com.example.entity.Candidate;
import com.example.entity.Question;
import com.example.repository.AnswerRepository;
import com.example.repository.CandidateRepository;
import com.example.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {
    @Autowired
    private  CandidateRepository candidateRepository;
    @Autowired
    private  AnswerRepository answerRepository;

    private  QuestionRepository questionRepository;




    public Candidate createCandidate(Candidate candidate) {
        if (emailExists(candidate.getEmailId())) {
            throw new RuntimeException("Email ID exists, please enter a different email ID.");
        }
        return candidateRepository.save(candidate);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Candidate getCandidateById(int id) {
        return candidateRepository.findById(id).orElse(null);
    }


    public Candidate updateCandidate(int id, Candidate candidateDetails) {
        return candidateRepository.findById(id).map(candidate -> {
            candidate.setName(candidateDetails.getName());
            candidate.setEmailId(candidateDetails.getEmailId());
            candidate.setStarted(candidateDetails.isStarted());
            candidate.setSubmit(candidateDetails.isSubmit());
            return candidateRepository.save(candidate);
        }).orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + id));
    }


    public void deleteCandidate(int id) {
        candidateRepository.findById(id).ifPresentOrElse(
                candidateRepository::delete,
                () -> { throw new RuntimeException("Candidate not found with ID: " + id); }
        );
    }


    public String submitAnswer(int candidateId, List<Answer> answers) throws Exception {
        // Check if candidate exists
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new Exception("Candidate ID: " + candidateId + " doesn't exist"));

        // Check if the answers are already submitted
        if (Boolean.TRUE.equals(candidate.isSubmit())) {
            return "Answer already submitted.";
        }

        // Store all answers
        for (Answer answer : answers) {
            answer.setCandidateId(candidateId);
            answerRepository.save(answer);
        }

        // Mark the answers as submitted
        candidate.setSubmit(true);
        candidateRepository.save(candidate);

        // Compare answers and generate result
        long correctAnswers = answers.stream()
                .filter(answer -> {
                    Question question = questionRepository.findById(answer.getQuestionId()).orElse(null);
                    return question != null && question.getAnswer()==answer.getAnswer();
                }).count();

        // Return the appropriate message
        return correctAnswers > 6
                ? "Candidate ID: " + candidateId + " is selected for the next Round.\nCorrect Answers: " + correctAnswers + "\nIncorrect Answers: " + (answers.size() - correctAnswers)
                : "Candidate ID: " + candidateId + " is rejected in this Round.\nCorrect Answers: " + correctAnswers + "\nIncorrect Answers: " + (answers.size() - correctAnswers);
    }

    private boolean emailExists(String emailId) {
        return candidateRepository.findByEmailId(emailId).isPresent();
    }

    public boolean isEmailIdExists(String emailId) {
        return candidateRepository.findByEmailId(emailId).isPresent();
    }
}
