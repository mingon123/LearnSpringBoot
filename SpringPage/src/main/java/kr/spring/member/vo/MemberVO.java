package kr.spring.member.vo;

import java.io.IOException;
import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString(exclude = {"photo"}) // 제외할 데이터는 exclude = {배열형식}으로 입력
public class MemberVO {
	private long mem_num;
	@Pattern(regexp="^[A-Za-z0-9]{4,14}$")
	private String id;
	private String nick_name;
	private String authority;
	@NotBlank
	private String name;
	@Pattern(regexp="^[A-Za-z0-9]{4,12}$")
	private String passwd;
	@NotBlank
	private String phone;
	@Email
	@NotBlank
	private String email;
	@Size(min=5,max=5)
	private String zipcode;
	@NotBlank
	private String address1;
	@NotBlank
	private String address2;
	private byte[] photo;
	private String photo_name;
	private Date reg_date;
	private Date modify_date;
	
	// 비밀번호 변경시 현재 비밀번호를 저장하는 용도로 사용
	@Pattern(regexp="^[A-Za-z0-9]{4,12}$")
	private String now_passwd;
	
	// 별명이 미등록되 있으면 id 반환, 별명이 등록되어 있으면 별명 반환
	public String getUserName() {
		if(nick_name==null) return id;
		return nick_name;
	}
	
	// 비밀번호 일치 여부 체크
	public boolean isCheckedPassword(String userPasswd) {
		if(getAuthorityOrdinal() > 1 && passwd.equals(userPasswd)) {
			return true;
		}
		return false;
	}
	
	// 회원 등급 체크
	public int getAuthorityOrdinal() {
		if(authority == null) return -1;
		
		if(authority.equals(UserRole.INACTIVE.getValue())) {
			return UserRole.INACTIVE.ordinal(); // 탈퇴 : 0
		}else if(authority.equals(UserRole.SUSPENDED.getValue())) {
			return UserRole.SUSPENDED.ordinal(); // 정지 : 1
		}else if(authority.equals(UserRole.USER.getValue())) {
			return UserRole.USER.ordinal(); // 일반회원 : 2
		}else if(authority.equals(UserRole.ADMIN.getValue())) {
			return UserRole.ADMIN.ordinal(); // 관리자 : 3
		}else {
			return -1;
		}
	}
	
	// 이미지 BLOB 처리
	public void setUpload(MultipartFile upload) throws IOException {
		log.debug("<<파일처리 진입>>");
		// MultipartFile -> byte[]
		setPhoto(upload.getBytes());
		// 파일 이름
		setPhoto_name(upload.getOriginalFilename());
	}

}
