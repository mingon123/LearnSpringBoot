package kr.spring.member.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.spring.member.vo.MemberVO;

@Mapper
public interface MemberMapper {
	// 회원관리 - 일반회원
	@Select("SELECT spmember_seq.nextval FROM dual")
	public Long selectMemNum();
	@Insert("INSERT INTO spmember (mem_num,id,nick_name) VALUES (#{mem_num},#{id},#{nick_name})")
	public void insertMember(MemberVO member);
	public void insertMemberDetail(MemberVO member);
	public MemberVO selectIdAndNickName(Map<String, String> map);
	public MemberVO selectCheckMember(String id);
	public MemberVO selectMember(Long mem_num);
	public void updateMember(MemberVO member);
	public void updateMemberDetail(MemberVO member);
	public void updatePassword(MemberVO member);
	public void deleteMember(Long mem_num);
	public void deleteMemberDetail(Long mem_num);
	// 자동 로그인 해제
	public void deleteRememberMe(String id);
	// 비밀번호 찾기
	public void updateRandomPassword(MemberVO member);
	// 프로필 이미지 업데이트
	public void updateProfile(MemberVO member);
	
}
