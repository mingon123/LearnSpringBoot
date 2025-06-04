package kr.spring.board.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.spring.board.vo.BoardVO;

@Mapper
public interface BoardMapper {
	public void insertBoard(BoardVO board);
	@Select("SELECT COUNT(*) FROM aboard")
	public Integer getBoardCount();
	public List<BoardVO> getBoardList(Map<String, Integer> map);
	public BoardVO getBoard(Long num);
	public void updateBoard(BoardVO board);
	public void deleteBoard(Long num);
}
