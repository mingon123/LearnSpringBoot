package kr.spring.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodoController {
	@GetMapping("/")
	public String init() {
		return "redirect:/todo/main";
	}
	
	@GetMapping("/todo/main")
	public String getMain() {
		return "todoList";
	}
	
	
}
