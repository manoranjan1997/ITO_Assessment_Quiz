package com.example.service;

import com.example.entity.Question;
import com.example.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    public Question createQuestion(Question question) {
        // Ensure the question has both options and an answer
        if (question.getOption1() != null && question.getOption2() != null && question.getOption3() != null && question.getOption4() != null ) {
            return questionRepository.save(question);
        }
        return null; // Return null if the question is invalid
    }

    public List<Question> createQuestions(List<Question> questions) {
        // Filter out questions without options and answers
        List<Question> validQuestions = questions.stream()
                .filter(q -> q.getOption1() != null && q.getOption2()!=null && q.getOption3()!=null && q.getOption4()!=null )
                .collect(Collectors.toList());

        // Save all valid questions and return the list
        return questionRepository.saveAll(validQuestions);
    }


    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(int id) {
        return questionRepository.findById(id).orElse(null);
    }

    public Question updateQuestion(int id, Question questionDetails) {
        Question question = getQuestionById(id);
        if (question != null) {
            question.setQuestion(questionDetails.getQuestion());
            question.setOption1(questionDetails.getOption1());
            question.setOption2(questionDetails.getOption2());
            question.setOption3(questionDetails.getOption3());
            question.setOption4(questionDetails.getOption4());
            question.setAnswer(questionDetails.getAnswer());
            return questionRepository.save(question);
        }
        return null;
    }

     public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    public boolean deleteQuestion(int id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) { questionRepository.deleteById(id); return true;
        }
        return false;
    }

    public Optional<Question> findQuestionById(int id) {
        return questionRepository.findById(id);
    }
}
