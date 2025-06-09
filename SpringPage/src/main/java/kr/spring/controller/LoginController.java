package kr.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.form.LoginForm;

@Controller
@RequestMapping("/login") // 중간 경로
public class LoginController {
	
	@GetMapping // /login 으로 처리
	public String showLogin(@ModelAttribute LoginForm form) {
		// templates 디렉토리의 login.html 반환
		return "login";		
	}
	
}
