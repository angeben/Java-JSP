package examsApp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import examsApp.model.Account;
import examsApp.model.Exam;
import examsApp.model.Question;
import examsApp.payload.exam.ExamPayload;
import examsApp.payload.exam.ExamViewPayload;
import examsApp.payload.exam.QuestionPayload;
import examsApp.service.AccountService;
import examsApp.service.ExamService;
import examsApp.service.QuestionService;
import examsApp.util.constants.ExamError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", allowedHeaders = "Host, Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-XSRF-TOKEN, Origin, Access-Control-Request-Origin, Access-Control-Request-Method, Access-Control-Request-Headers, Access-Control-Allow-Origin, access-control-allow-origin, Access-Control-Allow-Credentials, access-control-allow-credentials, Access-Control-Allow-Headers, access-control-allow-headers, Access-Control-Allow-Methods, access-control-allow-methods") 
@Tag(name = "Exam Controller", description = "Controller for exam and questions management")
@Slf4j
public class ExamController {
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;

    @CrossOrigin
    @PostMapping(value = "/exams/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Create an Exam")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewPayload> addExam(@Valid @RequestBody ExamPayload examDTO, Authentication auth) {
        try {
            Exam exam = new Exam();
            exam.setName(examDTO.getName());
            exam.setDescription(examDTO.getDescription());
            String email = auth.getName();
            Optional<Account> opAccount = accountService.findByEmail(email);
            Account user = opAccount.get();
            exam.setAccount(user);
            exam = examService.save(exam);
            ExamViewPayload examViewDTO = new ExamViewPayload(exam.getId(), exam.getName(), exam.getDescription(), null);
            return ResponseEntity.ok(examViewDTO);

        } catch (Exception e) {
            log.debug(ExamError.ADD_EXAM_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/exams", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Get exams information")
    @SecurityRequirement(name = "angeben-exams-app")
    public List<ExamViewPayload> get_Exams(Authentication auth){
        // Get logged in user
        String username = auth.getName();
        Optional<Account> opUser = accountService.findByEmail(username);
        Account user = opUser.get();
        // Get exams
        List<ExamViewPayload> exams = new ArrayList<>();
        for(Exam exam: examService.findByAccount_id(user.getId())){
            List<Question> exam_questions = questionService.findByExamId(exam.getId());
            exams.add(new ExamViewPayload(exam.getId(), exam.getName(), exam.getDescription(), exam_questions));
        }
        
        return exams;
    }

    @GetMapping(value = "/exams/{exam_id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Get exam information by id")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewPayload> exam_by_id(@PathVariable long exam_id, Authentication auth){
        // Get logged in user
        String username = auth.getName();
        Optional<Account> opUser = accountService.findByEmail(username);
        Account user = opUser.get();
        // Get exam
        Optional<Exam> opExam = examService.findById(exam_id);
        Exam exam;
        // Check errors
        if(opExam.isPresent())
            exam = opExam.get();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        if(user.getId() != exam.getAccount().getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        // Create DTO
        List<Question> exam_questions = questionService.findByExamId(exam.getId());
        ExamViewPayload examViewDTO = new ExamViewPayload(exam.getId(), exam.getName(), exam.getDescription(), exam_questions);
        return ResponseEntity.ok(examViewDTO);
    }

    @PutMapping(value = "/exams/{exam_id}/update", consumes = "application/json", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Update exam information by id")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewPayload> update_Exam(@Valid @RequestBody ExamPayload examDTO, @PathVariable long exam_id, Authentication auth){
        // Get logged in user
        String username = auth.getName();
        Optional<Account> opUser = accountService.findByEmail(username);
        Account user = opUser.get();
        // Get exam
        Optional<Exam> opExam = examService.findById(exam_id);
        Exam exam;
        // Check errors
        if(opExam.isPresent())
            exam = opExam.get();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        if(user.getId() != exam.getAccount().getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        // Update exam
        exam.setName(examDTO.getName());
        exam.setDescription(examDTO.getDescription());
        exam = examService.save(exam);
        // Create DTO
        List<Question> exam_questions = questionService.findByExamId(exam.getId());
        ExamViewPayload examViewDTO = new ExamViewPayload(exam.getId(), exam.getName(), exam.getDescription(), exam_questions);
        return ResponseEntity.ok(examViewDTO);
    }

    @DeleteMapping(value = "/exams/{exam_id}/delete")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "202", description = "Accepted")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Delete exam by id")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<String> delete_Exam(@PathVariable long exam_id, Authentication auth){
        // Get logged in user
        String username = auth.getName();
        Optional<Account> opUser = accountService.findByEmail(username);
        Account user = opUser.get();
        // Get exam
        Optional<Exam> opExam = examService.findById(exam_id);
        Exam exam;
        // Check errors
        if(opExam.isPresent())
            exam = opExam.get();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        if(user.getId() != exam.getAccount().getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);

        // Delete exam
        examService.deleteExam(exam);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @CrossOrigin
    @PostMapping(value = "/exams/{exam_id}/add-questions", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Add questions to an exam")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewPayload> addQuestions(@Valid @RequestBody QuestionPayload questionDTO, @PathVariable long exam_id, Authentication auth) {
        // Get user account
        String username = auth.getName();
        Optional<Account> opUser = accountService.findByEmail(username);
        Account user = opUser.get();
        // Get exam
        Optional<Exam> opExam = examService.findById(exam_id);
        Exam exam;
        if(opExam.isPresent())
            exam = opExam.get();
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        // Check authority for editing exam
        if(user.getId() != exam.getAccount().getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        
        // Save the new question
        Question question = new Question();
        question.setQuestion_body(questionDTO.getQuestion_body());
        question.setAnswer_a(questionDTO.getAnswer_a());
        question.setAnswer_b(questionDTO.getAnswer_b());
        question.setAnswer_c(questionDTO.getAnswer_c());
        question.setRight_answer(questionDTO.getRight_answer());
        question.setExam(exam);
        questionService.save(question);

        // Get all questions from the exam and return payload
        List<Question> exam_questions = questionService.findByExamId(exam_id);
        ExamViewPayload examViewPayload = new ExamViewPayload(exam_id, exam.getName(), exam.getDescription(), exam_questions);
        return ResponseEntity.ok(examViewPayload);
    }

}
