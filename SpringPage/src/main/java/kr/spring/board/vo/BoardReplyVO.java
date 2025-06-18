package kr.spring.board.vo;

import kr.spring.member.vo.MemberVO;
import kr.spring.util.DurationFromNow;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardReplyVO {
	private long re_num;
	private String re_content;
	private String re_date;
	private String re_mdate;
	private String re_ip;
	private long board_num;
	private long mem_num;
	
	// 로그인한 상태에서 클릭한 사람의 정보 읽기,
	// 로그인하지 않으면 0 전달
	private long click_num;
	private int refav_cnt; // 댓글 좋아요수
	private int resp_cnt; // 대댓글수
	
	private MemberVO memberVO;
	
	public void setRe_date(String re_date) {
		this.re_date = DurationFromNow.getTimeDiffLabel(re_date);
	}
	public void setRe_mdate(String re_mdate) {
		this.re_mdate = DurationFromNow.getTimeDiffLabel(re_mdate);
	}
}
