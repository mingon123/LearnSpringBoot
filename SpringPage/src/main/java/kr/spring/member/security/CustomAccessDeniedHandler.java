package kr.spring.member.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // 입력하면 빈 자동설정 대상이 됨
public class CustomAccessDeniedHandler implements AccessDeniedHandler{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// {}는 SLF4J(Simple Logging Facade for Java)에서 제공하는 플레이스홀더
		// 문자열 결합보다 성능이 더 좋음
		// 문자열 결합 : log.error("오류 발생 : " + accessDeniedException.toString()); 
		// 플레이스홀더 이용 : log.error("오류 발생 : {}", accessDeniedException.toString());
		
		log.debug("<<예외 발생 페이지>> : {}", request.getRequestURI());
		log.error("<<예외 발생>> : {}", accessDeniedException.toString());
		
		if(accessDeniedException instanceof InvalidCsrfTokenException | accessDeniedException instanceof MissingCsrfTokenException) {
			if(request.getRequestURI().equals("/member/logout")) {
				response.sendRedirect("/main/main");
				return;
			}
			
			FlashMap flashMap = new FlashMap();
			flashMap.put("accessMsg", "CSRF TOKEN 미사용 또는 오류");
			FlashMapManager flashMapManager = new SessionFlashMapManager();
			flashMapManager.saveOutputFlashMap(flashMap, request, response);
			response.sendRedirect(request.getContextPath()+"/accessDenied");
			return;			
		}
		response.sendRedirect(request.getContextPath()+"/accessDenied");

	}

}
