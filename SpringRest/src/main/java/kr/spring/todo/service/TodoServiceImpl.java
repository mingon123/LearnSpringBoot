package kr.spring.todo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.todo.dao.TodoMapper;
import kr.spring.todo.vo.TodoVO;

@Service
@Transactional
public class TodoServiceImpl implements TodoService{

	@Autowired
	private TodoMapper todoMapper;
	
	@Override
	public List<TodoVO> selectList() {
		return todoMapper.selectList();
	}

	@Override
	public void insertTodo(String todo) {
		todoMapper.insertTodo(todo);
	}

	@Override
	public void updateTodo(TodoVO vo) {
		todoMapper.updateTodo(vo);
	}

	@Override
	public void deleteTodo(Long id) {
		todoMapper.deleteTodo(id);
	}

}
