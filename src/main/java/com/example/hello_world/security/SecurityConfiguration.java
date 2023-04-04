package com.example.hello_world.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/add-discount-code").hasRole("ADMIN")
                .antMatchers("/profil").hasAnyRole("ADMIN", "USER")
                .antMatchers("/purchase/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/list-discount-code").permitAll()
                .antMatchers("/list-used-discount-code").permitAll()
                .antMatchers("/wyswietl/**").permitAll()
                .antMatchers("/list-users").permitAll()
                .antMatchers("/weryfikacja").permitAll()
                .antMatchers("/logowanie2").permitAll()
                .antMatchers("/registration").permitAll()
                .antMatchers("/oauth_login").permitAll()
                .antMatchers("/").permitAll()
                .and()
                    .formLogin()
                    .loginPage("/logowanie")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .loginProcessingUrl("/logowanie").permitAll()
                    .defaultSuccessUrl("/profil", true)
                    .failureHandler(myAuthenticationFailureHandler)
                    .permitAll()
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                .and()
                    .oauth2Login()
                    .loginPage("/logowanie").permitAll();
        }



    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}