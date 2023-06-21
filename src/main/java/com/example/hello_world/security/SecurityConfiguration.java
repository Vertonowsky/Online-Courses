package com.example.hello_world.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

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
                .expressionHandler(newWebSecurityExpressionHandler()) //Set expresion handler to include custom role hierarchy
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/add-discount-code").hasRole("ADMIN")
                .antMatchers("/profil").hasAnyRole("ADMIN", "USER")
                .antMatchers("/purchase/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/list-discount-code").permitAll()
                .antMatchers("/list-used-discount-code").permitAll()
                .antMatchers("/wyswietl/**").permitAll()
                .antMatchers("/kurs/getCourses").permitAll()
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



    // Create Role hierarchy
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        //String hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER";
        String hierarchy = "ROLE_ADMIN > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }


    // Include role hierarchy in Spring Web Expressions - add the roleHierarchy instance to the WebSecurityExpressionHandler
    @Bean
    public DefaultWebSecurityExpressionHandler newWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }


}