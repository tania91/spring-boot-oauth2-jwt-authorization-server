package com.oaut2.flter;



import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.oaut2.constants.Constants;
import com.oaut2.entity.TokenEntity;
import com.oaut2.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.MalformedJwtException;



import org.apache.commons.lang3.StringUtils;


import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter{
	private final String privateKey;
	private final TokenService tokenService;

	  public JWTAuthorizationFilter(final String privateKey, AuthenticationManager authenticationManager, TokenService tokenService) {
	    super(authenticationManager);
	    this.privateKey = privateKey;
	    this.tokenService = tokenService;
	  }

	  @Override
	  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
	    log.debug("doFilterInternal()");
	    UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
	    String header = request.getHeader(Constants.TOKEN_HEADER);

	    if (StringUtils.isEmpty(header) || !header.startsWith(Constants.TOKEN_PREFIX)) {
	      filterChain.doFilter(request, response);
	      return;
	    }

	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    filterChain.doFilter(request, response);
	  }

	  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
	   
	    String token = request.getHeader(Constants.TOKEN_HEADER);
	    if (StringUtils.isNotEmpty(token)) {
	      try {
	    	  byte[] signingKey = privateKey.getBytes();
	    	  if(token.indexOf("Bearer ")== -1) {
	    		  try {
						int delete = tokenService.delete(token.substring(token.indexOf(" ")+1));
					  } catch (Exception exception) {
						  HttpSession session = request.getSession();
				    	  session.setAttribute("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				    	  session.setAttribute("message", exception.getMessage());
					  }
	    		  HttpSession session = request.getSession();
		    	  session.setAttribute("status", HttpServletResponse.SC_FORBIDDEN);
		    	  session.setAttribute("message", "The token structure is not correct");
	    	  }
	    	  List<TokenEntity> tokenEntitySSO = tokenService.findSSO(token.substring(token.indexOf(" ")+1)); 
	    	  
	    	  if(!tokenEntitySSO.isEmpty()) {
		    	  Jws<Claims> parsedToken = Jwts.parser()
		            .setSigningKey(signingKey)
		            .parseClaimsJws(token.replace("Bearer ", ""));
	
		    	  String username = parsedToken
		            .getBody()
		            .getSubject();
	
		    	  List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
		            .get("roles")).stream()
		            .map(authority -> new SimpleGrantedAuthority((String) authority))
		            .collect(Collectors.toList());
		    	  Integer id = (Integer) parsedToken.getBody()
		    		.get("id");
		    	  
		    	  HttpSession session = request.getSession();
		    	  session.setAttribute("id", id);
		    	  
		        if (StringUtils.isNotEmpty(username)) {
		          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
		          return usernamePasswordAuthenticationToken;
		        }
	        
	    	  }else {
	    		  HttpSession session = request.getSession();
	    		  session.setAttribute("status", HttpServletResponse.SC_FORBIDDEN);
	    		  session.setAttribute("message", "Unrecognized token");
	    	  }
	        
	    	  
	      } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
	    	  HttpSession session = request.getSession();
	    	  session.setAttribute("status", HttpServletResponse.SC_UNAUTHORIZED);
	    	  session.setAttribute("message", exception.getMessage());
	      } catch (Exception exception) {
	    	  HttpSession session = request.getSession();
	    	  session.setAttribute("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    	  session.setAttribute("message", exception.getMessage());
	      }
	    }

	    return null;
	  }
}
