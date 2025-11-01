package ys.member.contorller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ys.member.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


}
