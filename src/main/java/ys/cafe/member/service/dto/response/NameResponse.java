package ys.cafe.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ys.cafe.member.domain.vo.MemberName;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NameResponse {

    @Schema(description = "성", example = "김")
    private String lastName;

    @Schema(description = "이름", example = "철수")
    private String firstName;

    @Schema(description = "전체 이름", example = "김철수")
    private String fullName;

    public static NameResponse from(MemberName name) {
        return new NameResponse(
            name.getLastName(),
            name.getFirstName(),
            name.getFullName()
        );
    }
}
