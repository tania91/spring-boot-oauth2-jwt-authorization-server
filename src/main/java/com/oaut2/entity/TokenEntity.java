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
@Table(name = "tokensso")
@EntityListeners(AuditingEntityListener.class)
public class TokenEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO )
	@Column(name ="id", nullable = false)
	private  Integer id;
	
	@Column(name ="ACCESS_TOKEN", length=1000)
	private String acces_toekn;
	
	@Column(name ="REFRESH_TOKEN", length=1000)
	private String refresh_token;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAcces_toekn() {
		return acces_toekn;
	}

	public void setAcces_toekn(String acces_toekn) {
		this.acces_toekn = acces_toekn;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
	
}
