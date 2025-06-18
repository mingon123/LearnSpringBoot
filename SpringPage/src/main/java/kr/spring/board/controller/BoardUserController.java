package kr.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;
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
	public String getList(@RequestParam(defaultValue="1") int pageNum,
						  @RequestParam(defaultValue="1") int order,
						  @RequestParam(defaultValue="") String category,
						  String keyfield, String keyword, Model model){
		log.debug("<<게시판 목록>> category : {}, order : {}",category,order);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("category", category);
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		
		// 전체, 검색 레코드수
		int count = boardService.selectRowCount(map);
		
		// 페이지 처리
		PagingUtil page = new PagingUtil(keyfield,keyword,pageNum,count,20,10,"list","&category="+category+"&order="+order);
		List<BoardVO> list = null;
		if(count > 0) {
			map.put("order", order);
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			
			list = boardService.selectList(map);
		}
		
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		model.addAttribute("order", order);
		model.addAttribute("keyfield", keyfield);
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", category);
		
		return "views/board/boardList";
	}
	
	// 게시판 상세
	@GetMapping("/detail")
	public String getDetail(long board_num, Model model) {
		log.debug("<<게시판 상세>> board_num : {}", board_num);
		
		// 해당 글의 조회수 증가
		boardService.updateHit(board_num);
		
		BoardVO board = boardService.selectBoard(board_num);
		
		// HTML 태그를 허용하지 않음
		board.setTitle(StringUtil.useNoHtml(board.getTitle()));
		
		// 내용에 태그를 허용하지 않으면서 줄바꿈 처리(summernote 사용시 주석 처리)
//		board.setContent(StringUtil.useBrNoHtml(board.getContent()));
		
		model.addAttribute("board", board);
		
		return "views/board/boardView";
	}
	
	// 파일 다운로드
	@GetMapping("/file")
	public String download(long board_num, HttpServletRequest request, Model model) {
		BoardVO board = boardService.selectBoard(board_num);
		// 컨텍스트 경로상의 절대경로 구하기
		String path = request.getServletContext().getRealPath("/assets//upload")+"/"+board.getFilename();
		File downloadFile = new File(path);
		
		model.addAttribute("downloadFile", downloadFile);
		model.addAttribute("filename", board.getFilename());
		
		return "downloadView";
	}
	
	// 게시판 수정 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/update")
	public String formUpdate(long board_num, Model model, @AuthenticationPrincipal PrincipalDetails principal) {
		BoardVO boardVO = boardService.selectBoard(board_num);
		if(principal.getMemberVO().getMem_num() != boardVO.getMem_num()) {
			// 로그인한 회원번호와 작성자 회원번호 불일치
			return "views/commmon/accessDenied";
		}
		
		model.addAttribute("boardVO", boardVO);
		
		return "views/board/boardModify";
	}
	
	// 게시글 수정
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/update")
	public String submitUpdate(@Valid BoardVO boardVO, 
					BindingResult result, 
					HttpServletRequest request, 
					Model model,
					@AuthenticationPrincipal PrincipalDetails principal) 
								throws IllegalStateException, IOException {
		log.debug("<<글 수정>> : {}", boardVO);
		
		BoardVO db_board = boardService.selectBoard(boardVO.getBoard_num());
		// 로그인한 회원번호와 작성자 회원번호 일치 여부 체크
		if(principal.getMemberVO().getMem_num() != db_board.getMem_num()) {
			return "views/common/accessDenied";
		}
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			// 유효성 체크 결과 오류 필드 출력
			ValidationUtil.printErrorFields(result);
			// title 또는 content가 입력되지 않아 유효성 체크에 걸리면 파일 정보를 잃어버리기 때문에 폼을 호출할 때 파일을 다시 셋팅
			boardVO.setFilename(db_board.getFilename());
			return "views/board/boardModify";
		}
		
		// 파일명 셋팅
		boardVO.setFilename(FileUtil.createFile(request, boardVO.getUpload()));
		// ip셋팅
		boardVO.setIp(request.getRemoteAddr());
		
		// 글 수정
		boardService.updateBoard(boardVO);
		
		if(boardVO.getUpload()!=null && !boardVO.getUpload().isEmpty()) {
			// 수정전 파일 삭제 처리
			FileUtil.removeFile(request, db_board.getFilename());
		}
		
		// View에 표시할 메시지
		model.addAttribute("message", "글 수정 완료");
		model.addAttribute("url", request.getContextPath()+"/board/detail?board_num="+boardVO.getBoard_num());
		
		return "views/common/resultAlert";
	}
	
	// 게시글 삭제
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete")
	public String submitDelete(long board_num, 
				HttpServletRequest request,
				@AuthenticationPrincipal PrincipalDetails principal) {
		log.debug("<<게시글 삭제>> board_num : {}", board_num);
		
		BoardVO db_board = boardService.selectBoard(board_num);
		// 로그인한 회원번호와 작성자 회원번호 일치 여부 체크
		if(principal.getMemberVO().getMem_num() != db_board.getMem_num()) {
			return "views/common/accessDenied";
		}
		
		// 글 삭제
		boardService.deleteBoard(board_num);
		
		if(db_board.getFilename() != null) {
			// 파일 삭제
			FileUtil.removeFile(request, db_board.getFilename());
		}
		return "redirect:/board/list";
	}
	
	
	
}
