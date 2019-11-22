package com.oaut2.service;

import com.oaut2.entity.UserEntity;
import com.oaut2.entity.UserTempEntity;
import com.oaut2.repository.UserRepository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.oaut2.repository.UserTempRepository;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class UserService implements UserDetailsService {
	

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserTempRepository userTempRepository;
	
	@PersistenceContext
    private EntityManager manager;
	
	/***************
	 * Buscar por nombre con User.class
	 */
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		
		 PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		 UserEntity user = userRepository.findByUsername(userId);
		if(user == null){
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		UserDetails detalle = new org.springframework.security.core.userdetails.User(user.getUsername(), passwordEncoder.encode(user.getPassword()), getAuthority(user.getRole()));
		saveTemp(user.getUsername(), user.getPassword());
		return detalle;
	}
	
	/****************
	 * Devolver el rol 
	 * @return
	 */
	private List<SimpleGrantedAuthority> getAuthority(String role) {
		return Arrays.asList(new SimpleGrantedAuthority(role));
	}
	
	/*****************
     * Devolver todos los usuarios
	 */
	public List<UserEntity> findAll() {
		
		List<UserEntity> list = new ArrayList<>();
		userRepository.findAll().iterator().forEachRemaining(list::add);
		return list;
	}
	
	/**************
	 * Devolver el usuario temporal
	 */
	public List<UserTempEntity> findAllTemp() {
		List<UserTempEntity> usuarioTamp = new ArrayList<UserTempEntity>(0);
		
		userTempRepository.findAll().iterator().forEachRemaining(usuarioTamp::add);
		return usuarioTamp;	
	}
	
	/**************
	 * Devolver usuario temporal desde un filtro
	 */
	public List<UserTempEntity> findAllTemp(UserTempRepository tempRepository) {
		List<UserTempEntity> usuarioTamp = new ArrayList<UserTempEntity>(0);
		
		tempRepository.findAll().iterator().forEachRemaining(usuarioTamp::add);
		return usuarioTamp;	
	}
	
	/*************
	 * Guerdar usurio temporal
	 * @param username
	 * @param password
	 */
	public void saveTemp(String username, String password)  {
		
		List<UserTempEntity> usuarioTampLista = findAllTemp();
		if(usuarioTampLista.size()!= 0 ) {
			userTempRepository.deleteAll();
		}
		
		UserTempEntity usuarioTemp = new UserTempEntity();
		usuarioTemp.setUsername(username);
		usuarioTemp.setPassword(password);
		
		
		userTempRepository.save(usuarioTemp);
	    
		
	}
	
	/*****************
	 * Buscar usuario por nombre para el fltro
	 * @param username
	 * @param userRep
	 * @return
	 * @throws UsernameNotFoundException
	 */
	public UserEntity findByName(String username, UserRepository userRep) throws UsernameNotFoundException {
		UserEntity user = userRep.findByUsername(username);
		return user;
	}
	
	/*****************
	 * Buscar usuario por nombre 
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException
	 */
	public UserEntity findByName(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByUsername(username);
		return user;
	}
	
	/****************
	 * Buscar usuario por id 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<UserEntity> findById(int id) throws Exception {
		
		List<UserEntity> list = new ArrayList<>();
		
		try {
			
			String queryString  = "SELECT * FROM usuariosSSO WHERE ID =" + id;
			
			list = manager.createNativeQuery(queryString,  UserEntity.class)
                    .getResultList();
			
		}catch(Exception e){
			throw new Exception(e.getMessage());
		};
		
		return list;
	}

	
	
	/*************
	 * Guerdar usurio
	 * @param user
	 * @return
	 */
	public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
	
	/****************
	 * Buscar usuario por rol 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<UserEntity> findByRol() throws Exception {
		
		List<UserEntity> list = new ArrayList<>();
		
		try {
			
			String queryString  = "SELECT * FROM usuariossso WHERE ROLE != 'ADMIN' ";
			
			list = manager.createNativeQuery(queryString,  UserEntity.class)
                    .getResultList();
			
		}catch(Exception e){
			throw new Exception(e.getMessage());
		};
		
		return list;
	}
//	//Eliminar usuario
//	public void delete(long id) {
//		// TODO Auto-generated method stub
//		
//	}

	
	
}
