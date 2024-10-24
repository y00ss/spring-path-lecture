package com.springpath.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authRequest ->
            authRequest.requestMatchers("/cashcards/**")
                    .hasAnyRole("CARD-OWNER", "SYSTEM")) // aggiunta ruolo CARD-OWNER
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()); //todo implementare
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails sysPrincipalUser = users
                .username("system-principal")
                .password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER")
                .build();
        UserDetails secSysPrincipalUser = users
                .username("system-principal-02")
                .password(passwordEncoder.encode("abc123"))
                .roles("NON-OWNER") // No roles for now
                .build();
        return new InMemoryUserDetailsManager(sysPrincipalUser, secSysPrincipalUser);
    }
}
