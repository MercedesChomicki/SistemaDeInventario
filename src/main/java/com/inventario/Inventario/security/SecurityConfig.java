package com.inventario.Inventario.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * .csrf().disable() --> Deshabilita la protección contra ataques CSRF (Cross-Site Request Forgery).
 * .requestMatchers("") --> Para los endpoints que no necesitan autenticación
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    /** Codifica contraseñas. Se utiliza para cifrar la contraseña
     * y almacenar el usuario en la DB */
    @Bean
    public BCryptPasswordEncoder pwdEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Propósito del proveedor de autenticación:
     * - Cargar el registro de usuario de la tabla para proporcionarselo
     * al administrador de autenticacion (AuthManager)
     * - Configura como se condifica la contraseña y cómo cargar el registro del cliente
     */
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(pwdEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    /**
     * Es responsable de comprobar que las credenciales de inicio de
     * sesión sean válidas, es decir, validar al usuario.
     */
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /** Se utiliza para especificar qué patrones de URL requieren
     * seguridad y cuáles no */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors( Customizer.withDefaults() )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register","/login", "/products").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(login -> login
                        .defaultSuccessUrl("/products", true) // URL donde se va a dirigir al iniciar sesión
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout").permitAll()
                )
                // TODO empieza la parte de configuración de sesión: https://www.youtube.com/watch?v=pmSJTrOWi7w minuto 40:29
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
