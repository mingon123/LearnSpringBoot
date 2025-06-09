package kr.spring.todo.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.spring.todo.vo.TodoVO;

@Mapper
public interface TodoMapper {
	@Select("SELECT * FROM stodo ORDER BY id DESC")
	public List<TodoVO> selectList();
	@Insert("INSERT INTO stodo (id,todo) VALUES (stodo_seq.nextval,#{todo})")
	public void insertTodo(String todo);
	public void updateTodo(TodoVO vo);
	@Delete("DELETE FROM stodo WHERE id=#{id}")
	public void deleteTodo(Long id);
}
