package examsApp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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
    @ApiResponse(responseCode = "400", description = "Please add valid name and description")
    @ApiResponse(responseCode = "201", description = "Exam created")
    @Operation(summary = "Create an Exam")
    @SecurityRequirement(name = "angeben-exams-app")
    public ResponseEntity<ExamViewDTO> addExam(@Valid @RequestBody ExamDTO examDTO, Authentication authentication) {
        try {
            Exam exam = new Exam();
            exam.setName(examDTO.getName());
            exam.setDescription(examDTO.getDescription());
            String email = authentication.getName();
            Optional<Account> optionaAccount = accountService.findByEmail(email);
            Account account = optionaAccount.get();
            exam.setAccount(account);
            exam = examService.save(exam);
            ExamViewDTO examViewDTO = new ExamViewDTO(exam.getId(), exam.getName(), exam.getDescription());
            return ResponseEntity.ok(examViewDTO);

        } catch (Exception e) {
            log.debug(ExamError.ADD_EXAM_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }



}
