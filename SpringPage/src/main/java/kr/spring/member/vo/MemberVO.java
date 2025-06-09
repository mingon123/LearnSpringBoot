package kr.spring.member.vo;

import java.io.IOException;
import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"photo"}) // 제외할 데이터는 exclude = {배열형식}으로 입력
public class MemberVO {
	private long mem_num;
	private String id;
	private String nick_name;
	private String authority;
	private String name;
	private String passwd;
	private String phone;
	private String email;
	private String zipcode;
	private String address1;
	private String address2;
	private byte[] photo;
	private String photo_name;
	private Date reg_date;
	private Date modify_date;
	
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
		// MultipartFile -> byte[]
		setPhoto(upload.getBytes());
		// 파일 이름
		setPhoto_name(upload.getOriginalFilename());
	}

}
