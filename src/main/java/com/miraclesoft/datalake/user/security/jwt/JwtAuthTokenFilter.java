package com.miraclesoft.datalake.user.security.jwt;

import com.miraclesoft.datalake.user.security.services.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthTokenFilter.class);


    @Value("${test.app.jwtSecret}")
    private String jwtSecret;

    @Value("${test.app.jwtExpiration}")
    private int jwtExpiration;
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
			if (checkJWTToken(request, response)) {
				String jwt = getJwt(request);
				System.out.println("token from do filter for token validation: "+jwt);
				if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
					String username = tokenProvider.getUserNameFromJwtToken(jwt);

					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);
//					setUpSpringAuthentication(Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody());
				}
				else {
					SecurityContextHolder.clearContext();
				}
			}
			else {
				SecurityContextHolder.clearContext();
			}
        } catch (Exception e) {
            logger.error("Can NOT set user authentication -> Message: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }

        return null;
    }
    
    /**
   	 * Authentication method in Spring flow
   	 * 
   	 * @param claims
   	 */
   	private void setUpSpringAuthentication(Claims claims) {
   		@SuppressWarnings("unchecked")
   		List<String> authorities = (List<String>) claims.get("authorities");

   		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
   				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
   		SecurityContextHolder.getContext().setAuthentication(auth);

   	}

   	private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
   		String authenticationHeader = request.getHeader("Auth");
   		if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer "))
   			return false;
   		return true;
   	}
}

