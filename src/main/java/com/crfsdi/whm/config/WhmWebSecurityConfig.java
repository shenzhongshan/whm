package com.crfsdi.whm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsUtils;

import com.crfsdi.whm.filter.JWTLoginFilter;
import com.crfsdi.whm.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true,jsr250Enabled=true)
public class WhmWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WhmWebSecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略css.jq.img等文件
        web.ignoring().antMatchers("/**/*.html", "/**/*.htm", "/**/*.tl","/**/*.js","/**/*.png", "/css/**/*", "/img/**/*", "/js/**/*", "/vue-easytable/**/*","/third-party/**/*","/font/**/*");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JWTLoginFilter jwtLoginFilter = new JWTLoginFilter(authenticationManager());
		JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authenticationManager());
		jwtAuthFilter.setUserDetailsService(userDetailsService);
		http.cors().disable().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, "/users/signup","/users/resetAdminPwd","/users/resetAdmin","/wechat/bind","/wechat/login","/login").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and().headers().frameOptions().disable()
                .and()
                .addFilter(jwtLoginFilter)
                .addFilter(jwtAuthFilter);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(wechatAuthenticationProvider());
    	DaoAuthenticationProvider daoprovider = new DaoAuthenticationProvider();
    	daoprovider.setUserDetailsService(userDetailsService);
    	daoprovider.setPasswordEncoder(bCryptPasswordEncoder);
    	auth.authenticationProvider(daoprovider);
        //auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    
    
    private AuthenticationProvider wechatAuthenticationProvider() {
    	return new AuthenticationProvider() {
    	    @Override
    	    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	    	String username = authentication.getName();
    	    	String openid = authentication.getCredentials().toString();
    	    	if("wechat-user".equalsIgnoreCase(username)) {
    	    		UserDetails  usedetails = userDetailsService.loadUserByUsername(openid);
        	        if(isMatch(authentication, usedetails)){
        	            return new UsernamePasswordAuthenticationToken(usedetails ,authentication.getCredentials(),usedetails.getAuthorities());
        	        }
    	    	}
    	        return null;
    	    }

			@Override
    	    public boolean supports(Class<?> authentication) {
    	        return true;
    	    }

    	    private boolean isMatch(Authentication authentication,  UserDetails usedetails){
    	        if(usedetails != null)
    	            return true;
    	        else
    	            return false;
    	    }
       };
   }
    
/*	@SuppressWarnings("unchecked")
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin(CorsConfiguration.ALL);
		config.addAllowedHeader(CorsConfiguration.ALL);
		config.addAllowedMethod(CorsConfiguration.ALL);
		config.setMaxAge(60000L);
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}*/

}
