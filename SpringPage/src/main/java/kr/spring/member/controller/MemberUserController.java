package kr.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member") // 중간 경로
public class MemberUserController {
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private PasswordEncoder passwordEncoder; // 비밀번호 암호화
	
	// 자바빈(VO) 초기화
	@ModelAttribute
	public MemberVO initCommand() {
		return new MemberVO();
	}
	
	// 회원가입 폼 호출
	@GetMapping("/registerUser")
	public String form() {
		return "views/member/memberRegister";
	}
	
}
