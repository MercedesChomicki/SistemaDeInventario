package com.inventario.Inventario.security;

import com.inventario.Inventario.security.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * .csrf().disable() --> Deshabilita la protección contra ataques CSRF (Cross-Site Request Forgery).
 * .requestMatchers("") --> Para los endpoints que no necesitan autenticación
 * .sessionManagement() --> Para configurar el comportamiento de las sesiones

 * Políticas de creación (SessionCreationPolicy)
 *     - ALWAYS: Crea una sesion solo si aún no existe, sino reutiliza la existente
 *     - IF_REQUIRED: Crea una nueva sesion solo si es necesario
 *     - NEVER: No crea ninguna sesion, pero si ya existe la va a utilizar
 *     - STATELESS: No crea ninguna sesion, sino que todas las solicitudes las
 *          maneja de manera independiente y no guarda datos de las mismas

 * .invalidSessionUrl("") --> Si la sesion es invalida, es decir, si no se logra autenticar,
 *     no se logra crear una sesion o se crea una sesion erronea, se redirige al usuario a la url dada

 * .maximumSessions(1) --> Se permite más de una sesión en aplicaciones multiplataformas por ejemplo.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationFilter authFilter;

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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register","/auth/login", "/products").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
