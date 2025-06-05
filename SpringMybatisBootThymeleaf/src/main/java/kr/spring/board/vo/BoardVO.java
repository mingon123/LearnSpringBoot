package kr.spring.board.vo;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardVO {
	private long num;
	@NotBlank
	private String writer;
	@NotBlank
	private String title;
	@NotBlank
	private String passwd;
	@NotEmpty
	private String content;
	private Date reg_date;
}
