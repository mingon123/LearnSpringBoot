package kr.spring.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
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

	// 자바빈(VO) 초기화
	@ModelAttribute
	public BoardVO initCommand() {
		return new BoardVO();
	}
	
	@GetMapping("/insert.do")
	public String form() {
		return "insertForm";
	}
	
	// 글쓰기 처리
	@PostMapping("/insert.do")
	public String submit(@Valid BoardVO boardVO, BindingResult result) {
		log.debug("<<글쓰기>> : " + boardVO);
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return form();
		}
	
		// 글쓰기
		boardService.insertBoard(boardVO);
		
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
	
	// 글상세
	@GetMapping("/detail.do")
	public String detail(long num, Model model) {
		BoardVO board = boardService.getBoard(num);
		model.addAttribute("board", board);
		
		return "selectDetail";
	}
	
	// 수정 폼 호출
	@GetMapping("/update.do")
	public String formUpdate(long num, Model model) {
		model.addAttribute("boardVO", boardService.getBoard(num)); // 위는 변수 선언 후 넣기, 여기는 바로 넣기
		
		return "updateForm";
	}
	
	// 글 수정 처리
	@PostMapping("/update.do")
	public String submitUpdate(@Valid BoardVO boardVO, BindingResult result) {
		log.debug("<<글 수정>> : " + boardVO);
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return "updateForm";
		}
		
		// 비밀번호 일치 여부 체크
		BoardVO db_board = boardService.getBoard(boardVO.getNum());
		if(!db_board.getPasswd().equals(boardVO.getPasswd())) {
			result.rejectValue("passwd", "invalidPassword");
			
			return "updateForm";
		}
		
		// 글 수정
		boardService.updateBoard(boardVO);
		
		return "redirect:detail.do?num="+boardVO.getNum();
	}
	
	// 글 삭제 폼 호출
	@GetMapping("/delete.do")
	public String formDelete(BoardVO boardVO) {
		return "deleteForm";
	}
	
	// 글 삭제 처리
	@PostMapping("/delete.do")
	public String submitDelete(@Valid BoardVO boardVO, BindingResult result) {
		log.debug("<<글 삭제>> : " +boardVO);
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		// 비밀번호 전송 여부만 체크
		if(result.hasFieldErrors("passwd")) {
			return "deleteForm";
		}
		
		// 비밀번호 일치 여부 체크
		BoardVO db_board = boardService.getBoard(boardVO.getNum());
		if(!db_board.getPasswd().equals(boardVO.getPasswd())) {
			result.rejectValue("passwd", "invalidPassword");
			return "deleteForm";
		}

		// 글 삭제
		boardService.deleteBoard(boardVO.getNum());
		
		return "redirect:/list.do";
	}
}
