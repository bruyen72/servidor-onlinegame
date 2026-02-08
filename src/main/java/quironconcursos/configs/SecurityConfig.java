package quironconcursos.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import quironconcursos.filters.JwtAuthFilter;
import quironconcursos.services.common.JWTService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.url.frontend}")
    private String urlFrontend;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter jwtFilter() {
        return new JwtAuthFilter(jwtService, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/reset-password.html").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/auth/ticket").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auth/ticket-anonymous").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/auth/recover-password").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/auth/change-password").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/admin/users").hasAnyAuthority("ROLE_ROOT", "ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/api/admin/send-email").hasAnyAuthority("ROLE_ROOT", "ROLE_ADMINISTRATOR")

                        .requestMatchers("/ws").permitAll()

                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/ws", config);

        return source;
    }

}