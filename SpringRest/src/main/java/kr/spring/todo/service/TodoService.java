package kr.spring.todo.service;

import java.util.List;

import kr.spring.todo.vo.TodoVO;

public interface TodoService {
	public List<TodoVO> selectList();
	public void insertTodo(String todo);
	public void updateTodo(TodoVO vo);
	public void deleteTodo(Long id);
}
