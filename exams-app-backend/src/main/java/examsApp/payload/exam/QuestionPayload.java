package examsApp.payload.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionPayload {
    
    @NotBlank
    @Schema(description = "Body of the question", example = "When was America discovered?", requiredMode = RequiredMode.REQUIRED)
    private String question_body;

    @NotBlank
    @Schema(description = "Answer A", example = "1205", requiredMode = RequiredMode.REQUIRED)
    private String answer_a;

    @NotBlank
    @Schema(description = "Answer B", example = "1492", requiredMode = RequiredMode.REQUIRED)
    private String answer_b;

    @NotBlank
    @Schema(description = "Answer C", example = "2000", requiredMode = RequiredMode.REQUIRED)
    private String answer_c;

    @NotBlank
    @Schema(description = "Indicator of which is the right answer", example = "B", requiredMode = RequiredMode.REQUIRED)
    private String right_answer;

}
