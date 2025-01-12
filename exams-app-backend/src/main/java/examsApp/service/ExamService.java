package examsApp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import examsApp.model.Exam;
import examsApp.repository.ExamRepository;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public Exam save(Exam exam){
        return examRepository.save(exam);
    }
    
    public List<Exam> findByAccount_id(long id){
        return examRepository.findByAccount_id(id);
    }


    public Optional<Exam> findById(long id){
        return examRepository.findById(id);
    }

    public void deleteExam(Exam exam){
        examRepository.delete(exam);
    }
}
