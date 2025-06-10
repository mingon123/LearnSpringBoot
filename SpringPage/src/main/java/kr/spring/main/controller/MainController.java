package kr.spring.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {
	@GetMapping("/")
	public String init() {
		return "redirect:/main/main";
	}
	
	@GetMapping("/main/main")
	public String main(Model model) {
		return "views/main/main";
	}
}
