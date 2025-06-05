package kr.spring.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.todo.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/todo")
public class TodoRestController {
	/*
	REST(Representational State Transfer)의 약자.
	클라이언트와 서버 간의 두 컴퓨터 시스템이 인터넷을 통해 정보를 안전하게 교환하기 위해 사용하는 인터페이스.
	자원을 이름으로 구분하여 해당 자원의 상태(정보)를 주고받는 모든 것	
	
	HTTP URI(Uniform Resource Identifier)를 이용해서 자원(Resource) 명시
	HTTP Method(POST,GET,PUT,DELETE,PATCH 등)를 통해 자원(URI)에 대한 CRUD Operation을 적용
	
	Create : 데이터 생성(POST)
	Read   : 데이터 조회(GET)
	Update : 데이터 수정(PUT,PATCH)
	Delete : 데이터 삭제(DELETE)
	
	Client가 자원의 상태(정보)에 대한 조작을 요청하면
	Server는 이에 적절한 응답을 보냄
	REST에서 하나의 자원은 JSON,XML,TEXT,RSS 등 여러 형태의 응답을 받을 수 있음(일반적으로 JSON 혹은 XML 사용)
	
	@Controller 어노테이션과 @ResponseBody의 조합 -> @RestController
	*/
	
	@Autowired
	private TodoService todoService;
	
	// 할 일 등록
	
	
	
}
