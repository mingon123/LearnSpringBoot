package kr.spring.board.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardUserController {
	@Autowired
	private BoardService boardService;
	
	// 자바빈 초기화
	@ModelAttribute
	public BoardVO initCommand() {
		return new BoardVO();
	}
	
	// 게시판 글등록 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/write")
	public String form() {
		return "views/board/boardWrite";
	}
	
	// 글등록 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/write")
	public String submit(@Valid BoardVO boardVO, BindingResult result, HttpServletRequest request, @AuthenticationPrincipal PrincipalDetails principal, Model model) throws IllegalStateException, IOException {
		log.debug("<<게시판 글등록>> : {}",boardVO); // content부분은 안나오는데 VO에서 추가해서 사용해도 됨.
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			// 유효성 체크 결과 오류 필드 출력
			ValidationUtil.printErrorFields(result);
			return form();
		}
		
		// 회원번호 셋팅
		boardVO.setMem_num(principal.getMemberVO().getMem_num());
		// ip셋팅
		boardVO.setIp(request.getRemoteAddr());
		// 파일 업로드 처리
		boardVO.setFilename(FileUtil.createFile(request, boardVO.getUpload()));
		// 글등록
		boardService.insertBoard(boardVO);
		
		model.addAttribute("message", "글을 정상적으로 등록했습니다.");
		model.addAttribute("url", request.getContextPath()+"/board/list");
		
		return "views/common/resultAlert";
	}
	
	// 게시판 목록
	@GetMapping("/list")
	public String getList() {
		
		return "views/board/boardList";
	}
	
	
	
	
}
