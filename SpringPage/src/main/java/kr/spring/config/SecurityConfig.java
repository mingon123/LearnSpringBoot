package kr.spring.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig { // 객체 생성할 때 빈설정해서 여기서 생성해서 호출해서 사용
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		// HTTP 요청에 대한 보안 설정
		http.authorizeHttpRequests(authorize -> authorize
				//	/login은 인증이 필요하지 않음
				.requestMatchers("/login").permitAll()
				// 그 밖의 요청은 인증이 필요
				.anyRequest().authenticated())
			// csrf 비활성화
			.csrf((csrf) -> csrf.disable())
			// 폼 기반 로그인 설정
			.formLogin(form -> form
					// 커스텀 로그인 페이지 URL (templates/login.html)
					.loginPage("/login")
					// 로그인 처리 URL 지정
					.loginProcessingUrl("/authentication")
					// 사용자명의 name 속성 지정
					.usernameParameter("usernameInput")
					// 비밀번호의 name 속성 지정
					.passwordParameter("passwordInput")
					// 로그인 성공시 리다이렉션할 URL 지정
					.defaultSuccessUrl("/",true)
					// 로그인 실패시 리다이렉션할 URL 지정
					.failureUrl("/login?error"))
			// 로그아웃 설정
			.logout(logout -> logout
					// 로그아웃을 처리할 URL을 지정
					.logoutUrl("/logout")
					// 로그아웃 성공시 리다이렉트할 목적지
					.logoutSuccessUrl("/login?logout")
					// 로그아웃시 세션을 무효화
					.invalidateHttpSession(true)
					// 로그아웃시 쿠키를 삭제
					.deleteCookies("JSESSIONID")
					);
		
		return http.build();
	}
	


}
