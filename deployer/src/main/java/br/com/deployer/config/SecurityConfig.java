package br.com.deployer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.deployer.service.UsuarioService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UsuarioService usuarioService() {
		return new UsuarioService();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication()
//			.passwordEncoder(passwordEncoder())
//			.withUser("admin")
//			.password(passwordEncoder().encode("123"))
//			.roles("ADMIN");
		auth.userDetailsService(usuarioService()).passwordEncoder(passwordEncoder());
		
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.authorizeRequests()
				.anyRequest().authenticated()
		.and()
			.formLogin().loginPage("/login.xhtml").permitAll()
			.defaultSuccessUrl("/home.xhtml", true)
			.failureUrl("/login.xhtml?authError=true")
		.and()
			.logout().logoutSuccessUrl("/login.xhtml").logoutUrl("/logout")
		;
	}
}
