package com.example.controller;

import com.example.dto.CandidateAnswerRequest;
import com.example.entity.Answer;
import java.util.ArrayList;
import java.util.*;
import com.example.entity.Question;
import com.example.service.CandidateService;
import com.example.service.EvaluationService;
import com.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itoquiz/admin")
public class AdminController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestions(@RequestBody List<Map<String, Object>> questionInputs) {
        if (questionInputs == null) {
            return ResponseEntity.badRequest().body("Request body is null");
        }

        List<Question> createdQuestions = new ArrayList<>();
        StringBuilder successMessages = new StringBuilder();
        StringBuilder errorMessages = new StringBuilder();

        for (Map<String, Object> input : questionInputs) {
            String questionText = (String) input.get("question");
            List<String> options = (List<String>) input.get("options");
            int answer = (int) input.get("answer");

            System.out.println(questionText);

            // Validate question
            if (questionText != null && !questionText.trim().isEmpty() &&
                options != null && options.size() == 4
                    && options.stream().allMatch(option -> option != null && !option.trim().isEmpty())
                    && answer >= 1 && answer <= 4) {

                // Map options to entity fields
                Question q = new Question();
                q.setQuestion(questionText);
                q.setOption1(options.get(0));
                q.setOption2(options.get(1));
                q.setOption3(options.get(2));
                q.setOption4(options.get(3));
                q.setAnswer(answer);

                // Create question
                Question createdQuestion = questionService.createQuestion(q);
                if (createdQuestion != null) {
                    createdQuestions.add(createdQuestion);
                    successMessages.append("Question ").append(createdQuestion.getQuestionId())
                            .append(" generated successfully. ");
                } else {
                    errorMessages.append("Failed to Generate Question ").append(questionText)
                            .append(" - creation issue. ");
                }
            } else {
                errorMessages.append("Failed to Generate Question ").append(questionText)
                        .append(" - exactly four non-empty options required or invalid answer. ");
            }
        }

        if (createdQuestions.isEmpty()) {
            return ResponseEntity.badRequest().body(errorMessages.toString().trim());
        }
        if (errorMessages.length() > 0) {
            successMessages.append(" and, ").append(errorMessages.toString().trim());
        }
        return ResponseEntity.ok(successMessages.toString().trim());
    }




    @GetMapping("/questions")
    public ResponseEntity<?> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.ok("No Questions available");
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/question")
    public ResponseEntity<?> getQuestionById(@RequestParam int questionId) {
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            return ResponseEntity.ok("Invalid Question Number");
        }
        return ResponseEntity.ok(question);
    }


    @PutMapping("/questions")
    public ResponseEntity<?> updateQuestion(@RequestParam int questionId, @RequestBody Map<String, Object> questionInput) {
        if (questionInput == null) {
            return ResponseEntity.badRequest().body("Request body is null");
        }

        String questionText = (String) questionInput.get("question");
        List<String> options = (List<String>) questionInput.get("options");
        int answer = (int) questionInput.get("answer");

        // Validate question
        if (questionText != null && !questionText.trim().isEmpty() &&
                options != null && options.size() == 4
                && options.stream().allMatch(option -> option != null && !option.trim().isEmpty())
                && answer >= 1 && answer <= 4) {

            // Find existing question
            Optional<Question> existingQuestionOpt = questionService.findQuestionById(questionId);
            if (existingQuestionOpt.isPresent()) {
                Question existingQuestion = existingQuestionOpt.get();
                existingQuestion.setQuestion(questionText);
                existingQuestion.setOption1(options.get(0));
                existingQuestion.setOption2(options.get(1));
                existingQuestion.setOption3(options.get(2));
                existingQuestion.setOption4(options.get(3));
                existingQuestion.setAnswer(answer);

                // Update question
                try {
                    Question updatedQuestion = questionService.updateQuestion(existingQuestion);
                    return ResponseEntity.ok("Updated question number " + updatedQuestion.getQuestionId() + " successfully.");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to Update question number " + questionId);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Question Number");
            }
        } else {
            return ResponseEntity.badRequest().body("Failed to Update question number "+ questionId);
        }
    }



    @DeleteMapping("/question")
    public ResponseEntity<?> deleteQuestions(@RequestParam String questionId) {
        String[] idArray = questionId.split(",");
        List<Integer> successIds = new ArrayList<>();
        List<Integer> failedIds = new ArrayList<>();

        for (String idStr : idArray) {
            try {
                int id = Integer.parseInt(idStr.trim());
                if (questionService.deleteQuestion(id)) { // assuming deleteQuestion returns boolean for success
                    successIds.add(id);
                } else {
                    failedIds.add(id);
                }
            } catch (NumberFormatException e) {
                failedIds.add(Integer.parseInt(idStr.trim()));
            }
        }

        if (successIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete selected question");
        }

        if (failedIds.isEmpty()) {
            return ResponseEntity.ok("Deleted selected question successfully");
        }

        String successIdsStr = String.join(",", successIds.stream().map(String::valueOf).toArray(String[]::new));
        String failedIdsStr = String.join(",", failedIds.stream().map(String::valueOf).toArray(String[]::new));
        return ResponseEntity.ok(successIdsStr + " are deleted successfully and failed to delete " + failedIdsStr);
    }


    @PostMapping("/submitAnswerSheet")
    public ResponseEntity<?> submitAnswerSheet(@RequestBody CandidateAnswerRequest request) {
        try {
            String result = candidateService.submitAnswer(request.getCandidateId(), request.getAnswers());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateCandidate(@RequestParam int candidateId) {
        String result = evaluationService.evaluateCandidate(candidateId);
        return ResponseEntity.ok(result);
    }
}
