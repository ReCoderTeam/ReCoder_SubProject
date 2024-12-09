package com.ohgiraffers.refactorial.config;

import com.ohgiraffers.refactorial.common.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 정적 리소스에 대한 요청은 security 설정에서 제외되도록 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Autowired
    private AuthFailHandler authFailHandler;

    @Autowired
    private AuthSuccessHandler authSuccessHandler;

    @Bean
    public SecurityFilterChain configureFillter(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests(auth ->{
            // permitAll() : 허가 받지 않은 사용자들도 접근 할 수 있는 URL
            // 여기 작성하지 않는 url에 접근할 경우 모두 로그인이 필요하다고 판단되어 로그인으로 보낸다.
            auth.requestMatchers("auth/login","/user","/index","*","/user/**").permitAll();

            // /user/* 은 일반회원 권한을 가진 사람들만 접근 가능
//            auth.requestMatchers("/user/approvalPage",
//                    "/user/searchEmployeePage").hasAnyAuthority(UserRole.USER.getRole(),UserRole.ADMIN.getRole());

            // hasAnyAuthority(필요 권한) -> 해당 URL 은 ()의 권한을 가진 사람만 접근 할 수 있다.
//            auth.requestMatchers("/admin/*").hasAnyAuthority(UserRole.ADMIN.getRole());

            auth.anyRequest().authenticated();
        }).formLogin(login ->{
            login.loginPage("/auth/login");
            login.usernameParameter("member_id");
            login.passwordParameter("member_pwd");
            login.defaultSuccessUrl("/user",true);
            login.successHandler(authSuccessHandler);
            login.failureHandler(authFailHandler);
        }).sessionManagement(session ->{
            session.maximumSessions(2);         // session 의 허용 갯수 제한( 한 사용자가 동시에 여러 세션 활성화 )
            session.invalidSessionUrl("/");     // 세션이 만료 되었을 때 효청할 URL
            session.sessionFixation().newSession();       // 로그인 할때마다 새로운 세션 생성
        }).csrf(csrf -> csrf.disable());

        return http.build();
    }

}
