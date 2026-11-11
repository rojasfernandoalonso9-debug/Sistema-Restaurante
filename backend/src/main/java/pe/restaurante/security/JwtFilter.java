package pe.restaurante.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final UserDetailsService users;
    public JwtFilter(JwtService jwt,UserDetailsService users){this.jwt=jwt;this.users=users;}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
        String auth=request.getHeader("Authorization");
        if(auth!=null&&auth.startsWith("Bearer ")){try{String email=jwt.subject(auth.substring(7));var user=users.loadUserByUsername(email);var token=new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));SecurityContextHolder.getContext().setAuthentication(token);}catch(RuntimeException ignored){SecurityContextHolder.clearContext();}}
        chain.doFilter(request,response);
    }
}
