package examsApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import examsApp.model.Question;
import java.util.List;


public interface QuestionRepository extends JpaRepository<Question, Long>{
    List<Question> findByExam_id(long id);
}
