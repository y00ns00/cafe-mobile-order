package ys.member.contorller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ys.member.service.MemberService;
import ys.member.service.dto.request.MemberSignUpRequest;
import ys.member.service.dto.response.MemberResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    @Operation(summary = "회원 가입")
    @PostMapping(value = "/members/singup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponse> member(@Valid MemberSignUpRequest request) {
        return new ResponseEntity<>(memberService.signUp(request), HttpStatus.CREATED);
    }

}
