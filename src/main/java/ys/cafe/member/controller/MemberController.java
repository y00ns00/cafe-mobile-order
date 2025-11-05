package ys.cafe.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ys.cafe.member.service.MemberService;
import ys.cafe.member.service.dto.ChangeStatusRequest;
import ys.cafe.member.service.dto.request.MemberSignUpRequest;
import ys.cafe.member.service.dto.response.MemberResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입")
    @PostMapping(value = "/members/signup", produces = MediaType.APPLICATION_JSON_VALUE, consumes =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponse> member(@Validated @RequestBody MemberSignUpRequest request) {
        return new ResponseEntity<>(memberService.signUp(request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "회원 탈퇴를 요청합니다. 상태가 WITHDRAW_REQUESTED로 변경되며, 30일 이내 철회 가능합니다."
    )
    @PostMapping(value = "/members/{memberId}/withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponse> withdraw(
            @Parameter(description = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId
    ) {
        MemberResponse response = memberService.withdraw(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "회원 탈퇴 철회",
            description = "회원 탈퇴를 철회합니다. WITHDRAW_REQUESTED 상태에서만 철회 가능하며, ACTIVE 상태로 복구됩니다. DELETED 상태는 철회 불가능합니다."
    )
    @PostMapping(value = "/members/{memberId}/cancel-withdraw", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponse> cancelWithdraw(
            @Parameter(description = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId
    ) {
        MemberResponse response = memberService.cancelWithdraw(memberId);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "회원 상태 변경",
            description = "회원 상태 변경 (테스트용)"
    )
    @PutMapping(value = "/members/{memberId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponse> changeMemberStatus(
            @Parameter(description = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId,
            @RequestBody ChangeStatusRequest changeStatusRequest
    ) {
        MemberResponse response = memberService.changeStatus(memberId, changeStatusRequest.status());
        return ResponseEntity.ok(response);
    }
}