package com.oaut2.flter;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import com.oaut2.entity.UserEntity;
import com.oaut2.repository.UserRepository;
import com.oaut2.service.TokenService;
import com.oaut2.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class JWTRevokeFilter  extends UsernamePasswordAuthenticationFilter{
	
	 private final TokenService tokenService;
	 private final String privateKey;
	 private final AuthenticationManager authenticationManager;
	 private final UserRepository userRepository;
	
	 public JWTRevokeFilter( UserRepository userRepository, final String privateKey, TokenService tokenService,  AuthenticationManager authenticationManager ) {
		 	this.userRepository = userRepository;	
		 	this.privateKey = privateKey;
		    this.tokenService = tokenService;
		    this.authenticationManager = authenticationManager;
		    super.setFilterProcessesUrl("/oauth/tokens/revoke");
		  }
	 
	 @Override
	  public Authentication attemptAuthentication(HttpServletRequest request,
	      HttpServletResponse response) throws AuthenticationException {
		  UsernamePasswordAuthenticationToken authenticationToken = null;
		  
		
		  String token = request.getParameter("token");
		  if(StringUtils.isNotEmpty(token)) {
			  byte[] signingKey = privateKey.getBytes();
			  String name = null;
			  try {
				  Jws<Claims> parsedToken = Jwts.parser()
				  .setSigningKey(signingKey)
				  .parseClaimsJws(token.replace("Bearer ", ""));

		    	  name = parsedToken
		          .getBody()
		          .getSubject();
			  }catch(Exception ex) {
				  try {
					int delete = tokenService.delete(token.substring(token.indexOf(" ")+1));
				} catch (Exception exception) {
					HttpSession session = request.getSession();
		    	  session.setAttribute("status", HttpServletResponse.SC_FORBIDDEN);
		    	  session.setAttribute("message", exception.getMessage());
				}
			  }
			  
	    	  UserService service = new UserService();
			  UserEntity usuario = null;
			  try {
				  usuario  = service.findByName(name, userRepository);
			  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
			  String username = usuario.getUsername();
			  String password = usuario.getPassword();
			  authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		  }else {
			  authenticationToken = new UsernamePasswordAuthenticationToken("", "");
		  }
	
		    return authenticationManager.authenticate(authenticationToken);
	    
	  }

	  @Override
	  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
	      FilterChain filterChain, Authentication authentication) {
		  String token = request.getParameter("token");
		  
		  try {
			int delete = tokenService.delete(token.substring(token.indexOf(" ")+1));
		  } catch (Exception exception) {
			HttpSession session = request.getSession();
	    	  session.setAttribute("status", HttpServletResponse.SC_FORBIDDEN);
	    	  session.setAttribute("message", exception.getMessage());
		  }
	    
	    
	  }

}
