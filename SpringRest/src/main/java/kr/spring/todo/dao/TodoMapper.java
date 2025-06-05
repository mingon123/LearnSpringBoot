package kr.spring.todo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import kr.spring.todo.vo.TodoVO;

@Mapper
public interface TodoMapper {
	public List<TodoVO> selectList();
	@Insert("INSERT INTO stodo (id,todo) VALUES (stodo_seq.nextval,#{todo})")
	public void insertTodo(String todo);
	public void updateTodo(TodoVO vo);
	public void deleteTodo(Long id);
}
