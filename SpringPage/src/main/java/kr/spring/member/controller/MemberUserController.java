package kr.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.spring.member.security.CustomAccessDeniedHandler;
import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.member.vo.PrincipalDetails;
import kr.spring.util.AuthCheckException;
import kr.spring.util.FileUtil;
import kr.spring.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/member") // 중간 경로
public class MemberUserController {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    MemberUserController(CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    } // 비밀번호 암호화
	
	// 자바빈(VO) 초기화
	@ModelAttribute
	public MemberVO initCommand() {
		return new MemberVO();
	}
	
	// 회원가입 폼 호출
	@GetMapping("/registerUser")
	public String form() {
		return "views/member/memberRegister";
	}
	
	// 회원가입 처리
	@PostMapping("/registerUser")
	public String submit(@Valid MemberVO memberVO, BindingResult result, Model model, HttpServletRequest request) { // 주의: 모델은 springframework.ui꺼 사용해야 함
		log.debug("<<회원가입>> : " + memberVO);
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			// 유효성 체크 결과 오류 필드 출력
			ValidationUtil.printErrorFields(result);
			return form();
		}
		
		// Spring Security 암호화
		memberVO.setPasswd(passwordEncoder.encode(memberVO.getPasswd()));

		// 회원가입
		memberService.insertMember(memberVO);
		
		// 결과 메시지 처리
		model.addAttribute("accessTitle", "회원가입");
		model.addAttribute("accessMsg", "회원가입이 완료되었습니다.");
		model.addAttribute("accessBtn", "홈으로");
		model.addAttribute("accessUrl", request.getContextPath()+"/main/main");		
		
		return "views/common/resultView";
	}
	
	// 로그인 폼 호출
	@GetMapping("/login")
	public String formLogin() {
		return "views/member/memberLogin";
	}
	
	/*	
	@PreAuthorize
	메서드 호출 이전에 접근 권한을 검사. 
	메서드 실행 전에 주어진 SpEL(Spring Expression Language) 조건을 평가하여 접근을 허용할지 결정
	*/
	// MY페이지 호출
	@PreAuthorize("isAuthenticated()") // 로그인 되어있는지 확인
	@GetMapping("/myPage")
	public String getMyPage(@AuthenticationPrincipal PrincipalDetails principal, Model model) { // 로그인된 계정의 스프링 세션 정보를 읽어옴
		// 회원정보
		MemberVO member = memberService.selectMember(principal.getMemberVO().getMem_num());
		
		model.addAttribute("member", member);
		
		return "views/member/memberView";
	}
	
	// 회원정보수정 폼 호출
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/updateUser")
	public String formUpdate(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
		// 회원정보
		MemberVO memberVO = memberService.selectMember(principal.getMemberVO().getMem_num());
		
		model.addAttribute("memberVO", memberVO);		
		
		return "views/member/memberModify";
	}
	
	// 회원정보수정 처리
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/updateUser")
	public String submitUpdate(@Valid MemberVO memberVO, BindingResult result, @AuthenticationPrincipal PrincipalDetails principal) {
		log.debug("<<회원정보수정 : >> {}",memberVO);
		
		// 유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			ValidationUtil.printErrorFields(result);
			return "views/member/memberModify";
		}
		
		memberVO.setMem_num(principal.getMemberVO().getMem_num());
		// 회원정보수정
		memberService.updateMember(memberVO);
		
		// PrincipalDetails에 저장된 자바빈(VO)의 nick_name, email 정보 갱신
		principal.getMemberVO().setNick_name(memberVO.getNick_name());
		principal.getMemberVO().setEmail(memberVO.getEmail());
		
		return "redirect:/member/myPage";
	}
	
	// 프로필 사진 출력(로그인 전용)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/photoView")
	public String getProfile(@AuthenticationPrincipal PrincipalDetails principal, HttpServletRequest request, Model model) {
		try {
			MemberVO user = principal.getMemberVO();
			log.debug("<<photoView>> : {}", user);
			MemberVO memberVO = memberService.selectMember(user.getMem_num());
			viewProfile(memberVO, request, model);			
		} catch (Exception e) {
			getBasicProfileImage(request, model);
		}
		return "imageView";
	}
	
	
	// 프로필 사진 출력(회원번호 지정)
	@GetMapping("/viewProfile")
	public String getProfileByMem_num(long mem_num, HttpServletRequest request, Model model) {
		MemberVO memberVO = memberService.selectMember(mem_num);
		viewProfile(memberVO, request, model);
		
		return "imageView";
	}
	

	// 프로필 사진 처리를 위한 공통 코드
	public void viewProfile(MemberVO memberVO, HttpServletRequest request, Model model) {
		if(memberVO==null || memberVO.getPhoto_name()==null) {
			// DB에 저장된 프로필 이미지가 없기 때문에 기본 이미지 호출
			getBasicProfileImage(request, model);
		}else {
			model.addAttribute("imageFile", memberVO.getPhoto());
			model.addAttribute("filename", memberVO.getPhoto_name());
		}
	}
	
	// 기본 이미지 읽기
	public void getBasicProfileImage(HttpServletRequest request, Model model) {
		byte[] readbyte = FileUtil.getBytes(request.getServletContext().getRealPath("/assets/image_bundle/face.png"));
		model.addAttribute("imageFile", readbyte);
		model.addAttribute("filename", "face.png");
	}
	
	// 비밀번호 찾기
	@GetMapping("/sendPassword")
	public String sendPasswordForm() {
		return "views/member/memberFindPassword";
	}
	
	// 비밀번호 변경 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/changePassword")
	public String formChangePassword() {
		return "views/member/memberChangePassword";
	}
	
	// 비밀번호 변경	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/changePassword")
	public String submitChangePassword(@Valid MemberVO memberVO,
			BindingResult result,
			HttpServletRequest request,
			@AuthenticationPrincipal PrincipalDetails principal,
			Model model) {
		log.debug("<<비밀번호 변경>> : {}", memberVO);
		
		if(result.hasFieldErrors("now_passwd") || result.hasFieldErrors("passwd")) {
			ValidationUtil.printErrorFields(result);
			return formChangePassword();
		}
		
		// 회원번호 저장
		memberVO.setMem_num(principal.getMemberVO().getMem_num());
		
		MemberVO db_member = memberService.selectMember(memberVO.getMem_num());
		// 폼에서 전송한 현재 비밀번호와 DB에서 읽어온 비밀번호 일치 여부 체크
		if(!passwordEncoder.matches(memberVO.getNow_passwd(), db_member.getPasswd())) {
			result.rejectValue("now_passwd", "invalidPassword");
			return formChangePassword();
		}
		
		// 새비밀번호 암호화
		memberVO.setPasswd(passwordEncoder.encode(memberVO.getPasswd()));
		// 자동 로그인 해제를 위해 id가 필요
		memberVO.setId(db_member.getId());
		
		// 비밀번호수정
		memberService.updatePassword(memberVO);
		
		// View에 표시할 메시지
		model.addAttribute("message", "비밀번호 변경 완료(*재접속시 설정되어 있는 자동로그인 기능 해제)");
		model.addAttribute("url", request.getContextPath()+"/member/myPage");
		
		return "views/common/resultAlert";
	}
	
	// 회원 탈퇴 폼
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete")
	public String formDelete() {
		return "views/member/memberDelete";
	}
	
	// 회원 탈퇴
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete")
	public String submitDelete(
			@Valid MemberVO memberVO,BindingResult result,
			@AuthenticationPrincipal PrincipalDetails principal,
			HttpServletRequest request,	HttpServletResponse response,
			Model model) {
		log.debug("<<회원 탈퇴>> : {}", memberVO);
		
		// id,passwd 필드의 에러만 체크
		if(result.hasFieldErrors("id") || result.hasFieldErrors("passwd")) {
			ValidationUtil.printErrorFields(result);
			return formDelete();
		}
		
		MemberVO db_member = memberService.selectMember(principal.getMemberVO().getMem_num());
		// 비밀번호 일치 여부 체크
		try {	
			if(passwordEncoder.matches(memberVO.getPasswd(), db_member.getPasswd())) { // 일치
				// 인증 성공, 회원정보 삭제
				memberService.deleteMember(principal.getMemberVO().getMem_num());
				// 로그아웃
				new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
				
				model.addAttribute("accessTitle", "회원탈퇴");
				model.addAttribute("accessMsg", "회원 탈퇴를 완료했습니다.");
				model.addAttribute("accessBtn", "홈으로");
				model.addAttribute("accessUrl", request.getContextPath()+"/main/main");

				return "views/common/resultView";
			}
			// 인증 실패
			throw new AuthCheckException();
		}catch(AuthCheckException e) {
			result.reject("invalidIdOrPassword"); // field가 없기 때문에 reject, 있으면 rejectValue
			return formDelete();
		}
	}
	
	
}