package com.oaut2.flter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.oaut2.constants.Constants;
import com.oaut2.entity.TokenEntity;
import com.oaut2.entity.UserEntity;
import com.oaut2.entity.UserTempEntity;
import com.oaut2.repository.TokenRepository;
import com.oaut2.repository.UserRepository;
import com.oaut2.repository.UserTempRepository;
import com.oaut2.service.TokenService;
import com.oaut2.service.UserService;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	
	 private final AuthenticationManager authenticationManager;
	 private final String privateKey;
	 private final UserTempRepository userTempRepository;
	 private final UserRepository userRepository;
	 private final TokenRepository tokenRepository;
	 private final TokenService tokenService;
	 
	

	  public JWTAuthenticationFilter(UserRepository userRepository, UserTempRepository userTempRepository, 
			  String privateKey, AuthenticationManager authenticationManager, TokenRepository tokenRepository, 
			  TokenService tokenService) {
		this.userRepository = userRepository;
		this.userTempRepository = userTempRepository;
	    this.authenticationManager = authenticationManager;
	    this.privateKey = privateKey;
	    this.tokenRepository = tokenRepository;
	    this.tokenService = tokenService;
	    super.setFilterProcessesUrl("/oauth/tokens");
	  }
	
	  
	  @Override
	  public Authentication attemptAuthentication(HttpServletRequest request,
	      HttpServletResponse response) throws AuthenticationException {
		  UsernamePasswordAuthenticationToken authenticationToken = null;
		  if(request.getParameter("code") == null) {
				  String token = request.getParameter("refresh_token");
				  if(StringUtils.isNotEmpty(token)) {
					  byte[] signingKey = privateKey.getBytes();
					  try {
						int delete = tokenService.deleteRefreshToken(token.substring(token.indexOf(" ")+1));
					  } catch (Exception exception) {
						  HttpSession session = request.getSession();
				    	  session.setAttribute("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				    	  session.setAttribute("message", exception.getMessage());
					  }
					  Jws<Claims> parsedToken = Jwts.parser()
			            .setSigningKey(signingKey)
			            .parseClaimsJws(token.replace("", ""));

			    	  String name = parsedToken
			            .getBody()
			            .getSubject();
			    	  
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

		  }else {
			  UserService service = new UserService();
			  List<UserTempEntity> usuario = null;
			  try {
				  usuario  = service.findAllTemp(userTempRepository);
			  } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
			  String username = usuario.get(0).getUsername();
			  String password = usuario.get(0).getPassword();
			  authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		  }
		  return authenticationManager.authenticate(authenticationToken);
	  }

	  @Override
	  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
	      FilterChain filterChain, Authentication authentication) {
	    User user = ((User) authentication.getPrincipal());
	    UserService service = new UserService();
		UserEntity usuario = new UserEntity();
		try {
			  usuario = service.findByName(user.getUsername(), userRepository);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    List<String> roles = user.getAuthorities()
	        .stream()
	        .map(GrantedAuthority::getAuthority)
	        .collect(Collectors.toList());
	    byte[] signingKey = privateKey.getBytes();
	    // Se genera token
	    String token = Jwts.builder()
	        .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
	        .setHeaderParam("typ", Constants.TOKEN_TYPE)
	        .setIssuer(Constants.TOKEN_ISSUER)
	        .setAudience(Constants.TOKEN_AUDIENCE)
	        .setSubject(user.getUsername())
	        .setExpiration(new Date(System.currentTimeMillis() + 1*60000))
	        .claim("roles", roles)
	        .claim("id", usuario.getId())
	        .compact();

	    response.addHeader(Constants.TOKEN_HEADER, Constants.TOKEN_PREFIX + token);
	    // Se genera refreshtoken
	    String refreshtoken = Jwts.builder()
	        .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
	        .setHeaderParam("typ", Constants.TOKEN_TYPE)
	        .setIssuer(Constants.TOKEN_ISSUER)
	        .setAudience(Constants.TOKEN_AUDIENCE)
	        .setSubject(user.getUsername())
	        .setExpiration(new Date(System.currentTimeMillis() + 3*60000))
	        .claim("roles", roles)
	        .claim("id", usuario.getId())
	        .compact();
	    
	    response.addHeader(Constants.REFRESHTOKEN_HEADER, Constants.TOKEN_PREFIX + refreshtoken);
	    
	    List<TokenEntity> listadoTokens = new ArrayList<TokenEntity>(0);
	    TokenEntity dataToken = new TokenEntity();
	    dataToken.setAcces_toekn(token);
	    dataToken.setRefresh_token(refreshtoken);
	    tokenRepository.save(dataToken);
	    
	  }
}
