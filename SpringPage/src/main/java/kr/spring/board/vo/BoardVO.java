package kr.spring.board.vo;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import groovy.transform.ToString;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import kr.spring.member.vo.MemberVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// summernote 사용시만 content 제외
@ToString(excludes = {"content"})
public class BoardVO {
	private long board_num;
	@NotBlank
	private String title;
	@NotBlank
	private String category;
	@NotEmpty
	private String content;
	private int hit;
	private Date reg_date;
	private Date modify_date;
	private MultipartFile upload;
	private String filename;
	private String ip;
	private long mem_num;
	
	private MemberVO memberVO;
	
	// 숫자 형태의 category 값을 문자열로 변환
	public String getCategoryName() {
		String name;
		switch(category) {
		case "1" : name = "자바"; break;
		case "2" : name = "데이터베이스"; break;
		case "3" : name = "자바스크립트"; break;
		case "4" : name = "기타"; break;
		default : name = "분류오류";
		}
		return name;
	}
}
