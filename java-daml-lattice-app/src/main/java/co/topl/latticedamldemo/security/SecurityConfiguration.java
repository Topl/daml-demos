package co.topl.latticedamldemo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web.ignoring()
                                // Spring Security should completely ignore URLs starting with /resources/
                                .antMatchers("/resources/**");
        }

        @Bean
        @Order(1)
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .formLogin((form) -> form.loginProcessingUrl("/authenticate").permitAll()
                                                .loginPage("/login").permitAll())
                                .formLogin().permitAll().defaultSuccessUrl("/home").and()
                                .logout((logout) -> logout.permitAll())
                                .csrf().disable()
                                .authorizeHttpRequests((requests) -> requests
                                                .antMatchers("/home").hasAuthority("USER")
                                                .antMatchers("/home/**").hasAuthority("USER")
                                                .antMatchers("/admin").hasAuthority("ADMIN")
                                                .antMatchers("/admin/**").hasAuthority("ADMIN")
                                                .anyRequest()
                                                .denyAll())
                                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler()).and()
                                .build();
        }

        @Bean(name = "myPasswordEncoder")
        public PasswordEncoder getPasswordEncoder() {
                DelegatingPasswordEncoder delPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
                                .createDelegatingPasswordEncoder();
                BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
                delPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder);
                return delPasswordEncoder;
        }
}