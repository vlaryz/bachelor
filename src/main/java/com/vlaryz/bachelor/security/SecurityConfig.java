package com.vlaryz.bachelor.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests().antMatchers("/login").permitAll();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.csrf().disable().authorizeRequests().antMatchers("/login").permitAll().and().formLogin().loginProcessingUrl("/api/users/login");

        http.authorizeRequests().antMatchers("/api/users/token/refresh/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/cities/**",
                        "/api/cities/events/**",
                        "/api/cities/events/tickets/**")
                .permitAll();
//        http.authorizeRequests().antMatchers("/api/cities/**",
//                        "/api/cities/events/**",
//                        "/api/cities/events/tickets/**")
//                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER");
//        http.authorizeRequests().antMatchers(POST, "/api/cities/**",
//                        "/api/cities/events/**",
//                        "/api/cities/events/tickets/**")
//                .hasAnyAuthority("ROLE_USER");
//        http.authorizeRequests().antMatchers(PUT, "/api/cities/**",
//                        "/api/cities/events/**",
//                        "/api/cities/events/tickets/**")
//                .permitAll();
//        http.authorizeRequests().antMatchers("/api/cities/**",
//                        "/api/cities/events/**",
//                        "/api/cities/events/tickets/**")
//                .hasAnyAuthority("ROLE_USER");

        http.authorizeRequests().anyRequest().permitAll();
        http.addFilter(new AuthenticationFilter(authenticationManagerBean()));
        http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return super.userDetailsService();
//    }
}
