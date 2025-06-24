package connectforospa_usuarios.config;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TokenAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.replace("Bearer", "").trim().toUpperCase();

            List<SimpleGrantedAuthority> authorities = switch (token) {
                case "TOKEN_ADMIN"     -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
                case "TOKEN_MODERADOR" -> List.of(new SimpleGrantedAuthority("ROLE_MODERADOR"));
                case "TOKEN_SOPORTE"   -> List.of(new SimpleGrantedAuthority("ROLE_SOPORTE"));
                case "TOKEN_ANUNCIOS"  -> List.of(new SimpleGrantedAuthority("ROLE_ANUNCIOS"));
                default -> List.of();
            };

            if (!authorities.isEmpty()) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken("autenticado", null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
