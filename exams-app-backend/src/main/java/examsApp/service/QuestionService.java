package examsApp.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import examsApp.model.Question;
import examsApp.repository.QuestionRepository;

@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;

    public Question save(Question question){
        return questionRepository.save(question);
    }

    public void delete(Question question){
        questionRepository.delete(question);
    }

    public Optional<Question> findById(long id){
        return questionRepository.findById(id);
    }

    public List<Question> findByExamId(long id){
        return questionRepository.findByExam_id(id);
    }

}
