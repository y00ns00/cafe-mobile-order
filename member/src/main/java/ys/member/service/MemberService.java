package ys.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ys.member.persistence.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;



}
