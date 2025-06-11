package kr.spring.member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberRestController {
	@Autowired
	private MemberService memberService;
	
	// 아이디 중복 체크
	@GetMapping("/confirmId/{id}")
	public ResponseEntity<Map<String,String>> checkId(@PathVariable String id){ // 경로 {id}에 매개변수 id를 넣어줌
		log.debug("<<아이디 중복 체크>> : " + id);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		MemberVO member = memberService.selectIdAndNickName(map);
		if(id!=null) { // 아이디 체크
			if(member!=null) {
				// 아이디 중복
				mapAjax.put("result", "idDuplicated");
			}else {
				if(!Pattern.matches("^[A-Za-z0-9]{4,14}$", id)) {
					// 패턴 불일치
					mapAjax.put("result", "notMatchPattern");
				}else {
					// 패턴 일치하면서 아이디 미중복
					mapAjax.put("result", "idNotFound");
				}
			}
		}else {
			mapAjax.put("result", "error"); // 아이디 전달 안됨
		}
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
	// 별명 중복 체크
	@GetMapping("/confirmNickName/{nick_name}")
	public ResponseEntity<Map<String, String>> checkNickName(@PathVariable String nick_name){ // 원래는@Pathvariable("nick_name")을 입력해야 하지만 변수명이 같으면 생략 가능
		log.debug("<<별명 중복체크>> : " + nick_name);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("nick_name", nick_name);
		MemberVO member = memberService.selectIdAndNickName(map);
		if(nick_name!=null) { // 별명 체크
			if(member!=null) {
				// 별명 중복
				mapAjax.put("result", "nickDuplicated");
			}else {
				if(!Pattern.matches("^[ㄱ-ㅎ가-힣A-Za-z0-9]{2,10}$", nick_name)) {
					// 패턴 불일치
					mapAjax.put("result", "notMatchPattern");
				}else {
					// 패턴이 일치하면서 별명 미중복
					mapAjax.put("result", "nickNotFound");
				}
			}
		}else {
			mapAjax.put("result", "error");
		}		
		return new ResponseEntity<Map<String,String>>(mapAjax, HttpStatus.OK);
	}
	
}
