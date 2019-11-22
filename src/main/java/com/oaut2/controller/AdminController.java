package com.oaut2.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.oaut2.dto.User;
import com.oaut2.entity.UserEntity;
import com.oaut2.repository.UserRepository;
import com.oaut2.service.UserService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	 // Devolver todos los usurios 
	 @RequestMapping(value="/all/users", method = RequestMethod.GET)
	 public List<User> listRecipe() throws Exception{
		 
		List<User> users = new ArrayList<User>();
		//Se comprueba el rol del usuario 
		checkIdentity();
		 
        List<UserEntity> entity = userService.findByRol();
       
        entity.stream().forEach(x -> {
        	User user = new User();
        	
        	user.setId(x.getId());
      	  	user.setRole(x.getRole());
      	  	user.setUsername(x.getUsername());
      	  	user.setEmail(x.getEmail());
      	  	user.setApellidos(x.getApellidos());
      	  	user.setNombre(x.getNombre());
      	  	
      	  	users.add(user);
        });

  	  	return users;
	 }
	 
	 
	 private void checkIdentity() {
			SecurityContext context = SecurityContextHolder.getContext();
			Authentication authentication = context.getAuthentication();
			    
	        for (GrantedAuthority auth : authentication.getAuthorities()) {
	            if (!"ADMIN".equals(auth.getAuthority())) {
	            	throw new AccessDeniedException("Acceso denegado");
	            }	
	        }
		 }
}
