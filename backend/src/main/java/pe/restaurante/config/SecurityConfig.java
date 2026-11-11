package pe.restaurante.config;
import pe.restaurante.security.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;
import java.util.Arrays;
@Configuration @EnableMethodSecurity public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
    @Bean SecurityFilterChain filterChain(HttpSecurity http,JwtFilter jwtFilter)throws Exception{return http.csrf(c->c.disable()).cors(c->{}).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/api/auth/setup").permitAll().requestMatchers(HttpMethod.GET,"/api/health").permitAll().anyRequest().authenticated()).addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();}
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origin:http://localhost:5173,http://127.0.0.1:5173,http://localhost:4173,http://127.0.0.1:4173}") String origins){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).filter(origin->!origin.isEmpty()).toList());c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("Authorization","Content-Type"));UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
}
