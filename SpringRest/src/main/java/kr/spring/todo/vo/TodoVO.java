package kr.spring.todo.vo;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TodoVO {
	private long id;
	private String todo;
	private Date created;
	private int completed;
}
