package gov.cabinetoffice.gap.applybackend.security;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.interfaces.DecodedJWT;
import gov.cabinetoffice.gap.applybackend.dto.api.JwtPayload;
import gov.cabinetoffice.gap.applybackend.exception.JwkNotValidTokenException;
import gov.cabinetoffice.gap.applybackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * This class cannot be a Spring bean, otherwise Spring will automatically apply it to all
 * requests, regardless of whether they've been specifically ignored
 */
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // Check if auth header exists. If not, return without setting authentication in the security context
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        //verify the token
        String normalisedJwt = header.split(" ")[1];
        DecodedJWT decodedJWT = jwtService.decodedJwt(normalisedJwt);
        try {
            if (!jwtService.verifyToken(decodedJWT)) {
                chain.doFilter(request, response);
                return;
            }
        } catch (JwkException e) {
            throw new JwkNotValidTokenException("Token not valid");
        }
        //set the Security context, so we can access it everywhere
        JwtPayload jwtPayload = jwtService.decodeTheTokenPayloadInAReadableFormat(decodedJWT);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwtPayload,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

}