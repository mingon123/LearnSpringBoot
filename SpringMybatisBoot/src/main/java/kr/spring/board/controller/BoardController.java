package kr.spring.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;
import kr.spring.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BoardController {
	
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/")
	public String init() {
		return "redirect:/list.do";
	}
	
	@GetMapping("/list.do")
	public String getList(@RequestParam(defaultValue="1") int pageNum, Model model) {
		
		// 총레코드수		
		int count = boardService.getBoardCount();
		log.debug("<<게시판 목록 - count>> : " + count); // @slf4j를 선언해서 따로 log 변수를 선언하지 않아도 됨
		
		// 페이지 처리
		PagingUtil page = new PagingUtil(pageNum, count, 10, 10, "list.do");
		
		// 목록 호출
		List<BoardVO> list = null;
		if(count > 0) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = boardService.getBoardList(map);
		}
		
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());		
		
		return "selectList";
	}
}
