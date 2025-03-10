package com.inventario.Inventario.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * .csrf().disable() --> Deshabilita la protección contra ataques CSRF (Cross-Site Request Forgery).
 * .requestMatchers("") --> Para los endpoints que no necesitan autenticación
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors( Customizer.withDefaults() )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .defaultSuccessUrl("/products", true) // URL donde se va a dirigir al iniciar sesión
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout").permitAll()
                )
                // TODO empieza la parte de configuración de sesión: https://www.youtube.com/watch?v=pmSJTrOWi7w minuto 40:29
                .build();
    }
}
