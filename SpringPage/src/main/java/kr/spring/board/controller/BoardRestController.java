package kr.spring.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/board")
public class BoardRestController {
	@Autowired
	private BoardService boardService;
	
	// 부모글 업로드 파일 삭제
	@DeleteMapping("/deleteFile/{board_num}")
	public ResponseEntity<Map<String, String>> processFile(
					@PathVariable long board_num, 
					@AuthenticationPrincipal PrincipalDetails principal,
					HttpServletRequest request){
		Map<String, String> mapAjax = new HashMap<String, String>();
		
		if(principal==null) {
			mapAjax.put("result", "logout");
		}else {
			BoardVO db_board = boardService.selectBoard(board_num);
			// 로그인한 회원번호와 작성자 회원번호 일치 여부 체크
			if(principal.getMemberVO().getMem_num() != db_board.getMem_num()) {
				mapAjax.put("result", "wrongAccess");
			}else {
				boardService.deleteFile(board_num);
				FileUtil.removeFile(request, db_board.getFilename());
				
				mapAjax.put("result", "success");
			}
		}
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
	}
	
	// 부모글 좋아요 읽기
	@GetMapping("/getFav/{board_num}")
	public ResponseEntity<Map<String,Object>> getFav(
			@PathVariable long board_num,
			@AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<게시판 좋아요>> board_num : {}", board_num);
		
		Map<String,Object> mapAjax = new HashMap<String, Object>();
		BoardFavVO fav = new BoardFavVO();
		fav.setBoard_num(board_num);
		if(principal==null) { // 비로그인
			mapAjax.put("status", "noFav");
		}else { // 로그인
			// 로그인된 회원번호 셋팅
			fav.setMem_num(principal.getMemberVO().getMem_num());
			
			BoardFavVO boardFav = boardService.selectFav(fav);
			if(boardFav!=null) {
				mapAjax.put("status", "yesFav");
			}else {
				mapAjax.put("status", "noFav");
			}
		}
		mapAjax.put("count", boardService.selectFavCount(fav.getBoard_num()));
		
		return new ResponseEntity<Map<String,Object>>(mapAjax,HttpStatus.OK);
	}
	
	// 부모글 좋아요 등록/삭제
	@PostMapping("/writeFav")
	public ResponseEntity<Map<String,Object>> writeFav(
				@RequestBody BoardFavVO fav,
				@AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<게시판 좋아요 등록/삭제>> : {}", fav);
		
		Map<String,Object> mapAjax = new HashMap<String, Object>();
		if(principal==null) { // 비로그인
			mapAjax.put("result", "logout");
		}else { // 로그인
			// 로그인된 회원번호 셋팅
			fav.setMem_num(principal.getMemberVO().getMem_num());
			BoardFavVO boardFav = boardService.selectFav(fav);
			if(boardFav!=null) {
				boardService.deleteFav(fav);
				mapAjax.put("status", "noFav");
			}else {
				boardService.insertFav(fav);
				mapAjax.put("status", "yesFav");
			}			
			mapAjax.put("result", "success");
			mapAjax.put("count", boardService.selectFavCount(fav.getBoard_num()));
		}
		return new ResponseEntity<Map<String,Object>>(mapAjax,HttpStatus.OK);
	}
	
	// 댓글 등록
	@PostMapping("/writeReply")
	public ResponseEntity<Map<String, String>> writeReply(
			@RequestBody BoardReplyVO boardReplyVO,
			@AuthenticationPrincipal PrincipalDetails principal,
			HttpServletRequest request){
		log.debug("<<댓글 등록>> : {}",boardReplyVO);
		
		Map<String,String> mapAjax = new HashMap<String, String>();
		if(principal==null) {
			mapAjax.put("result", "logout");
		}else {
			// 회원번호 저장
			boardReplyVO.setMem_num(principal.getMemberVO().getMem_num());
			// ip 저장
			boardReplyVO.setRe_ip(request.getRemoteAddr());
			
			// 댓글 등록
			boardService.insertReply(boardReplyVO);
			mapAjax.put("result", "success");
		}
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
	}
	
}
