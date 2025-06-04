package kr.spring.board.vo;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardVO {
	private long num;
	private String writer;
	private String title;
	private String passwd;
	private String content;
	private Date reg_date;
}
