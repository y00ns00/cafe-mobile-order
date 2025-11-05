package ys.cafe.member.service.dto;

import jakarta.validation.constraints.Pattern;

public record ChangeStatusRequest(
        @Pattern(regexp = "ACTIVE|WITHDRAW_REQUESTED|DELETED")
        String status
) {
}
