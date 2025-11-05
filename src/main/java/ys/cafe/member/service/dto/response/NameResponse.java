package ys.cafe.member.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ys.cafe.member.domain.vo.MemberName;

public record NameResponse(

        @Schema(description = "성", example = "김")
        String lastName,

        @Schema(description = "이름", example = "철수")
        String firstName,

        @Schema(description = "전체 이름", example = "김철수")
        String fullName
) {

    public static NameResponse from(MemberName name) {
        return new NameResponse(
                name.getLastName(),
                name.getFirstName(),
                name.getFullName()
        );
    }
}
