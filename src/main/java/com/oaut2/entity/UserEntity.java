package com.oaut2.entity;


import java.util.Collection;
import java.util.HashSet;
import java.util.*;

import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "usuariosSSO")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements  UserDetails {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO )
	@Column(name ="id", nullable = false)
	private  Integer id;
	
	@Column(name ="NOMBRE")
	private String nombre;
	
	
	@Column(name ="APELLIDOS")
	private String apellidos;
	
	
	@Column(name ="username")
	private String username;
	
	
	@Column(name ="PASSWORD")
	private String password;
	
	
	@Column(name ="EMAIL")
	private String email;
	
	@Column(name="enabled")	
	private short enabled;

	@Column(name = "role")
	private String role;
	
	@Column(name = "id_client")
	private String id_client;
	
	
	public short getEnabled() {
		return enabled;
	}

	public void setEnabled(short enabled) {
		this.enabled = enabled;
	}

	public String getId_client() {
		return id_client;
	}

	public void setId_client(String id_client) {
		this.id_client = id_client;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (null != role && !role.isEmpty()) {
		      Set<GrantedAuthority> grantedAuthorityList = new HashSet<>();
		      for (String role : role.split(",")) {
		        grantedAuthorityList.add(new SimpleGrantedAuthority(role));
		      }
		      return grantedAuthorityList;
		    }
		    return Collections.emptyList();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	
	
}
