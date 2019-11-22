package com.oaut2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.oaut2.flter.JWTAuthenticationFilter;
import com.oaut2.flter.JWTAuthorizationFilter;
import com.oaut2.flter.JWTRevokeFilter;
import com.oaut2.repository.TokenRepository;
import com.oaut2.repository.UserRepository;
import com.oaut2.repository.UserTempRepository;
import com.oaut2.service.TokenService;
import com.oaut2.service.UserService;

import java.util.Arrays;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;
    
    private final UserService userServiceImpl;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final String privateKey;
    private final UserTempRepository userTempRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;

    @Autowired
    public SecurityConfig(
        @Value("${privateKey}") final String privateKey,
        final UserService userServiceImpl,
        final AuthenticationConfiguration authenticationConfiguration,
        final UserTempRepository userTempRepository,
        final UserRepository userRepository,
        final TokenService tokenService,
        final TokenRepository tokenRepository) {
      this.privateKey = privateKey;
      this.authenticationConfiguration = authenticationConfiguration;
      this.userServiceImpl = userServiceImpl;
      this.userTempRepository = userTempRepository;
      this.userRepository = userRepository;
      this.tokenService = tokenService;
      this.tokenRepository = tokenRepository;
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .parentAuthenticationManager(authenticationConfiguration.getAuthenticationManager())
          .userDetailsService(userServiceImpl)
          .passwordEncoder(encoder());
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
    	
      /***************
      ** Se quitan las comprobaciones de identidad de las llamadas al los ficheros de configuracion del fichero jsp
      ***************/
      web.ignoring().antMatchers( "/css/**");
      web.ignoring().antMatchers("/*.css");
	  web.ignoring().antMatchers("/*.js");
	  
	  /***************
       ** Se quita lo comprobacion de la identidad de usuario en las peticiones cuando van con el verbo OPTION
       ***************/
      web.ignoring().antMatchers(HttpMethod.OPTIONS, "/oauth/token");
      web.ignoring().antMatchers(HttpMethod.OPTIONS, "/oauth/tokens/revoke");
      web.ignoring().antMatchers(HttpMethod.OPTIONS, "/admin/**");
      
    }
    
    /***************
    ** Filtro para CORS
    ***************/
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
   	CorsFilter customCorsFilter() {
   		CorsFilter filter = new CorsFilter(corsConfigurationSource());
   	    return filter;
   	}
    
    /***********************
    ** Configuracion de comprobacion de la edentidad de usurio. 
    ** Se deshabilita CSRF, se añade la configuraicon de CORS
    ** Se declaran las llamadas que necesitan la comprobacion de autenticidad de usurio
    ** Se declara la redireccion a la pagina de login para el caso de SSO
    ** Se declara el logout
    ** Se declaran los filtros para la autentificacion y autorizacion del usuario
    * ***********************/
     
    @Override
    protected void configure(HttpSecurity http ) throws Exception {
      http
      	.csrf().disable()
		.cors().and()
          .requestMatchers()
              .antMatchers("/login", "/oauth/authorize","/oauth/tokens", "/oauth/tokens/revoke")
              .antMatchers("/admin/**")
              .and()
          .authorizeRequests()
              .anyRequest().authenticated()
              .and()
          .formLogin()
              .loginPage( "/login")
              .permitAll()
           .and()
	           .addFilter(new JWTAuthenticationFilter(userRepository,userTempRepository, privateKey, 
	        		   authenticationManager(), tokenRepository, tokenService))
	           .addFilter(new JWTAuthorizationFilter(privateKey, authenticationManager(), tokenService))
	           .addFilter(new JWTRevokeFilter(userRepository, privateKey, tokenService, authenticationManager()));

    }

    
   
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**************
     * Se recogen los datos de usurio incriptando el password
     */
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());
    }
    /**************
     * Incriptacion BCrypt para password
     */
    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    /**************
     * Configuracion de CORS
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() 
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://www.cocinarusa.es:8081"));
        configuration.setAllowedMethods(Arrays.asList("POST", "GET",  "PUT", "OPTIONS", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "x-www-form-urlencoded", "application/json", "Access-Control-Allow-Headers", "Accept", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Refreshtoken") );
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    } 
    

    
 
}
