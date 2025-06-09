package kr.spring.todo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.todo.service.TodoService;
import kr.spring.todo.vo.TodoVO;
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
	// @RequestBody를 이용해서 JSON 데이터를 VO 타입으로 변환하도록 지정
	@PostMapping("/write")
	public ResponseEntity<Map<String,String>> write(@RequestBody TodoVO todoVO){ // ResponseEntity : 데이터도 JSON 형식으로 보내지만, 상태정보도 함께 보냄(지금은 OK), 실패도 처리할 수 있음. todo.list.js에서 data 부분이 바뀜(JSON 형식으로 보내기 때문)
		log.debug("<<할 일 등록>> : " + todoVO);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		// 할 일 등록
		todoService.insertTodo(todoVO.getTodo());
		mapAjax.put("result", "success");
		
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
	}
	
	// 할 일 목록
	@GetMapping("/list")
	public ResponseEntity<List<TodoVO>> getList(){ // count 없기 때문에 map으로 안보내고 list로 보냄
		
		List<TodoVO> list = todoService.selectList();
		
		return new ResponseEntity<List<TodoVO>>(list,HttpStatus.OK);
	}
	
	// 할 일 수정
	@PutMapping("/update")
	public ResponseEntity<Map<String, String>> modify(@RequestBody TodoVO todoVO){ // @RequestBody json 문자열을 VO로 변경해줌
		log.debug("<<할 일 수정>> : " + todoVO);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		
		todoService.updateTodo(todoVO);		
		
		mapAjax.put("result", "success");
		
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
	}
	
	/*
	{id}는 값이 변수임을 나타냄
	@PathVariable은 URL의 변수 값이 변수 id에 바인딩 되도록 함
	id 이외에 추가적인 변수가 있다면 아래와 같이 명시 가능
	url은 /delete/id/{id}/name/{name} or /delete/{id}/{name}의 구성
	*/
	
	// 할 일 삭제
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable long id){ // id를 읽어서 {id}에 집어넣음 - @PathVariable : 경로상의 값
		log.debug("<<id>> : " + id);
		
		todoService.deleteTodo(id);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		
		mapAjax.put("result", "success");
		
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
	}
}
