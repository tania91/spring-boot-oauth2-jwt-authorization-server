package com.oaut2.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "usuarioTemporal")
@EntityListeners(AuditingEntityListener.class)
public class UserTempEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Id
	@Column(name ="ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO )
	private Integer id;
	
	@Column(name ="USERNAME")
	private String username;
	
	@Column(name ="PASSWORD")
	private String password;


	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


}
