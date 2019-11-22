package com.oaut2.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import com.oaut2.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Configuration
@EnableAuthorizationServer
@EnableWebMvc
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	 private final String privateKey;	 
	 private final String client_secret; 
	 private final String client_id;
	 private final AuthenticationManager authenticationManager;
	 private final UserService userService;
	 private JwtAccessTokenConverter jwtAccessTokenConverter;
	  
	@Autowired
	  public AuthorizationServerConfig(
	      @Value("${privateKey}") final String privateKey,
	      @Value("${client_id}") final String client_id,
	      @Value("${client_secret}") final String client_secret,
	      final UserService userService,
	      final AuthenticationConfiguration authenticationConfiguration) throws Exception {
	    this.privateKey = privateKey;
	    this.client_id =client_id;
	    this.client_secret = client_secret;
	    this.userService = userService;
	    this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
	  }
	
	 @Override
	  public void configure(
	      AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
	    oauthServer.tokenKeyAccess("permitAll()")
	        .checkTokenAccess("isAuthenticated()");
	  }

	  @Override
	  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		  clients .inMemory()
		  .withClient(client_id)
          .secret(client_secret)
          .authorizedGrantTypes("authorization_code", "refresh_token")
          .scopes("read", "write")
          .autoApprove(true)
          .redirectUris("http://www.cocinarusa.es:8081/PFG/oauth2.0/app/dest/html/#!/")
          .accessTokenValiditySeconds(5*60000) 
          .refreshTokenValiditySeconds(7*60000);  
	  }

	  @Override
	  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
	    endpoints
	        .authenticationManager(authenticationManager)
	        .accessTokenConverter(jwtAccessTokenConverter())
	        .userDetailsService(userService)
	        .tokenStore(tokenStore());

	    //Invalidate the session once the user has been authenticated
	    endpoints.addInterceptor(new HandlerInterceptorAdapter() {
	      @Override
	      public void postHandle(HttpServletRequest request,
	                             HttpServletResponse response, Object handler,
	                             ModelAndView modelAndView) throws Exception {
	        if (modelAndView != null
	                && modelAndView.getView() instanceof RedirectView) {
	          RedirectView redirect = (RedirectView) modelAndView.getView();
	          String url = redirect.getUrl();
	          if (url.contains("code=") || url.contains("error=")) {
	        	  
	            HttpSession session = request.getSession(false);
	            if (session != null) {
	              session.invalidate();
	            }
	          }
	        }
	      }
	    });
	  }

	  @Bean
	  public TokenStore tokenStore() {
	    return new JwtTokenStore(jwtAccessTokenConverter());
	  }
	  
	  @Primary
	  @Bean
	  public DefaultTokenServices tokenServices(final TokenStore tokenStore,
	      final ClientDetailsService clientDetailsService) {
	    DefaultTokenServices tokenServices = new DefaultTokenServices();
	    tokenServices.setSupportRefreshToken(true);
	    tokenServices.setTokenStore(tokenStore);
	    tokenServices.setClientDetailsService(clientDetailsService);
	    tokenServices.setAuthenticationManager(this.authenticationManager);
	    return tokenServices;
	  }


	  @Bean
	  public JwtAccessTokenConverter jwtAccessTokenConverter() {
		
	    if (jwtAccessTokenConverter != null) {
	      return jwtAccessTokenConverter;
	    }
	    jwtAccessTokenConverter = new JwtAccessTokenConverter();
	    jwtAccessTokenConverter.setSigningKey(privateKey);
	    return jwtAccessTokenConverter;
	  }

	
	
}