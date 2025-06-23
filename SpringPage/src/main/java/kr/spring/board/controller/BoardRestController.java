package kr.spring.board.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReFavVO;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.FileUtil;
import kr.spring.util.PagingUtil;
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
	
	// 댓글 목록
	@GetMapping("listReply/{board_num}/{pageNum}/{rowCount}")
	public ResponseEntity<Map<String,Object>> getList(
				@PathVariable long board_num,
				@PathVariable int pageNum,
				@PathVariable int rowCount,
				@AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<댓글 목록>> board_num : {}, pageNum : {}", board_num, pageNum);
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("board_num", board_num);
		
		// 전체 레코드수
		int count = boardService.selectRowCountReply(map);
		
		List<BoardReplyVO> list = null;
		if(count > 0) {
			PagingUtil page = new PagingUtil(pageNum, count, rowCount);
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			if(principal!=null) { // 좋아요할 때 처리
				map.put("mem_num", principal.getMemberVO().getMem_num());
			}else {
				map.put("mem_num", 0);
			}
			list = boardService.selectListReply(map);
		}else {
			list = Collections.emptyList(); // 비어있는 리스트를 만들어서 반환
		}
		Map<String,Object> mapAjax = new HashMap<String, Object>();
		mapAjax.put("count", count);
		mapAjax.put("list", list);
		// 로그인한 회원번호와 작성자 회원번호 일치 여부를 체크하기 위해 로그인한 회원번호 전송
		if(principal!=null) {
			mapAjax.put("user_num", principal.getMemberVO().getMem_num());
		}
		return new ResponseEntity<Map<String,Object>>(mapAjax,HttpStatus.OK);
	}
	
	// 댓글 수정
	@PutMapping("/updateReply")
	public ResponseEntity<Map<String,String>> modifyReply(
				@RequestBody BoardReplyVO reply,
				@AuthenticationPrincipal PrincipalDetails principal,
				HttpServletRequest request){
		log.debug("<<댓글 수정>> : {}",reply);
		
		Map<String,String> mapAjax = new HashMap<String, String>();
		
		BoardReplyVO db_reply = boardService.selectReply(reply.getRe_num());
		if(principal==null) { // 로그인이 안된 경우
			mapAjax.put("result", "logout");
		}else if(principal.getMemberVO().getMem_num()==db_reply.getMem_num()) {
			// 로그인 회원번호와 작성자 회원번호 일치
			// ip 저장
			reply.setRe_ip(request.getRemoteAddr());
			// 댓글 수정
			boardService.updateReply(reply);
			mapAjax.put("result", "success");
		}else {
			// 로그인 회원번호와 작성자 회원번호 불일치
			mapAjax.put("result", "wrongAccess");
		}
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);
	}
	
	// 댓글 삭제
	@DeleteMapping("/deleteReply/{re_num}")
	public ResponseEntity<Map<String, String>> deleteReply(
			@PathVariable long re_num,
			@AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<댓글 삭제>> re_num : {}",re_num);
		
		Map<String, String> mapAjax = new HashMap<String, String>();
		BoardReplyVO db_reply = boardService.selectReply(re_num);
		if(principal==null) {
			mapAjax.put("result", "logout");
		}else if(principal.getMemberVO().getMem_num()==db_reply.getMem_num()) {
			// 로그인한 회원번호와 작성자 회원번호 일치
			boardService.deleteReply(re_num);
			mapAjax.put("result", "success");
		}else {
			// 로그인한 회원번호와 작성자 회원번호 불일치
			mapAjax.put("result", "wrongAccess");
		}
		return new ResponseEntity<Map<String,String>>(mapAjax,HttpStatus.OK);		
	}
	
	// 댓글 좋아요 등록/삭제
	@PostMapping("/writeReFav")
	public ResponseEntity<Map<String,Object>> writeReFav(
			@RequestBody BoardReFavVO fav,
			@AuthenticationPrincipal PrincipalDetails principal){
		log.debug("<<댓글 좋아요 등록/삭제>> : {}",fav);
		
		Map<String,Object> mapAjax = new HashMap<String, Object>();
		
		if(principal == null) {
			mapAjax.put("result", "logout");
		}else {
			// 로그인된 회원번호 셋팅
			fav.setMem_num(principal.getMemberVO().getMem_num());
			
			BoardReFavVO boardReFav = boardService.selectReFav(fav);
			
			if(boardReFav!=null) {
				boardService.deleteReFav(fav);
				mapAjax.put("status", "noFav");
			}else {
				boardService.insertReFav(fav);
				mapAjax.put("status", "yesFav");
			}
			mapAjax.put("result", "success");
			mapAjax.put("count", boardService.selectReFavCount(fav.getRe_num()));
		}
		return new ResponseEntity<Map<String,Object>>(mapAjax,HttpStatus.OK);
	}
	
	
	
	
}
