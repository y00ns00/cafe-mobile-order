package ys.member.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpRequest {

    /**
     * (?=.*[a-zA-Z]) : 영문자 최소 1개 포함 (positive lookahead)
     * (?=.*[0-9]) : 숫자 최소 1개 포함
     * (?=.*[!@#$%?]) : 특수문자 최소 1개 포함
     * [a-zA-Z0-9!@#$%?]{8,20} : 허용된 문자로 8~20자
     */
    @Schema(description = "비밀번호", example = "password123!@#")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%?])[a-zA-Z0-9!@#$%?]{8,20}$",
            message = "비밀번호는 8~20자의 영문자, 숫자, 특수문자(!@#$%?)를 포함해야 합니다"
    )
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "성", example = "김")
    @NotBlank(message = "성은 필수입니다.")
    private String lastName;

    @Schema(description = "이름", example = "철수")
    @NotBlank(message = "이름은 필수입니다.")
    private String firstName;

    @Schema(description = "전화번호 (하이픈 포함 또는 미포함)", example = "010-1234-5678")
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(
            regexp = "^\\d{2,3}-?\\d{3,4}-?\\d{4}$",
            message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678 또는 01012345678)"
    )
    private String phoneNumber;

    @Schema(description = "성별 (MALE, FEMALE, OTHER)", example = "MALE")
    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(
            regexp = "^(MALE|FEMALE|OTHER)$",
            message = "성별은 MALE, FEMALE, OTHER 중 하나여야 합니다."
    )
    private String gender;

    @Schema(description = "생년월일 (yyyy-MM-dd 형식)", example = "1990-01-01")
    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(
            regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "생년월일은 yyyy-MM-dd 형식이어야 합니다. (예: 1990-01-01)"
    )
    private String birthDate;

}