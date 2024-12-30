package com.example.service;

import com.example.entity.Answer;
import com.example.entity.Candidate;
import com.example.entity.Evaluation;
import com.example.repository.AnswerRepository;
import com.example.repository.CandidateRepository;
import com.example.repository.EvaluationRepository;
import com.example.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EvaluationService {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;

    public Evaluation createEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    public String evaluateCandidate(int candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            return "Candidate ID doesn’t exist.";
        }

        List<Answer> answers = answerRepository.findByCandidateId(candidateId);

        // Debug: Print the size of the answers list
        System.out.println("Total Answers Submitted: " + answers.size());

        // Debug: Print each answer
        answers.forEach(answer -> System.out.println("Question ID: " + answer.getQuestionId() + ", Answer: " + answer.getAnswer()));

        long correctCount = answers.stream()
                .filter(answer -> questionRepository.findById(answer.getQuestionId())
                        .map(question -> {
                            int correctAnswer = question.getAnswer();
                            int candidateAnswer = answer.getAnswer();
                            return correctAnswer == candidateAnswer;
                        })
                        .orElse(false))
                .count();

        int totalQuestions = answers.size();
        int incorrectCount = (int)totalQuestions - (int) correctCount;

        // Debug: Print counts
        System.out.println("Correct Answers: " + correctCount);
        System.out.println("Incorrect Answers: " + incorrectCount);

        String resultMessage = correctCount > 6
                ? candidateId + " : " + candidate.getName() + " is selected for the next round."
                : candidateId + " : " + candidate.getName() + " is rejected for the next round.";

        resultMessage += "\nCorrect Answers: " + correctCount;
        resultMessage += "\nIncorrect Answers: " + incorrectCount;

        return resultMessage;
    }


}
