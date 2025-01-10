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
import examsApp.payload.exam.ExamDTO;
import examsApp.payload.exam.ExamViewDTO;
import examsApp.service.AccountService;
import examsApp.service.ExamService;
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

    @CrossOrigin
    @PostMapping(value = "/exams/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Create an Exam")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewDTO> addExam(@Valid @RequestBody ExamDTO examDTO, Authentication auth) {
        try {
            Exam exam = new Exam();
            exam.setName(examDTO.getName());
            exam.setDescription(examDTO.getDescription());
            String email = auth.getName();
            Optional<Account> opAccount = accountService.findByEmail(email);
            Account user = opAccount.get();
            exam.setAccount(user);
            exam = examService.save(exam);
            ExamViewDTO examViewDTO = new ExamViewDTO(exam.getId(), exam.getName(), exam.getDescription());
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
    public List<ExamViewDTO> get_Exams(Authentication auth){
        // Get logged in user
        String username = auth.getName();
        Optional<Account> opUser = accountService.findByEmail(username);
        Account user = opUser.get();
        // Get exams
        List<ExamViewDTO> exams = new ArrayList<>();
        for(Exam exam: examService.findByAccount_id(user.getId()))
            exams.add(new ExamViewDTO(exam.getId(), exam.getName(), exam.getDescription()));
        
        return exams;
    }

    @GetMapping(value = "/exams/{exam_id}", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Get exam information by id")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewDTO> exam_by_id(@PathVariable long exam_id, Authentication auth){
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
        ExamViewDTO examViewDTO = new ExamViewDTO(exam.getId(), exam.getName(), exam.getDescription());
        return ResponseEntity.ok(examViewDTO);
    }

    @PutMapping(value = "/exams/{exam_id}/update", consumes = "application/json", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "404", description = "Not found")
    @Operation(summary = "Update exam information by id")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewDTO> update_Exam(@Valid @RequestBody ExamDTO examDTO, @PathVariable long exam_id, Authentication auth){
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
        ExamViewDTO examViewDTO = new ExamViewDTO(exam.getId(), exam.getName(), exam.getDescription());
        return ResponseEntity.ok(examViewDTO);
    }

    @DeleteMapping(value = "/exams/{exam_id}/delete")
    @ApiResponse(responseCode = "200", description = "OK")
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

}
