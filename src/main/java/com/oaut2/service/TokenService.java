package com.oaut2.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.oaut2.entity.TokenEntity;


@Component
public class TokenService {
	@PersistenceContext
    private EntityManager manager;
 
	
	@Transactional
	public int delete(String valor) throws Exception {
		int deletedCount = 0;
		try {
			
			String queryString  = "DELETE FROM tokensso WHERE ACCESS_TOKEN = '" + valor + "'";
			
			deletedCount = manager.createNativeQuery(queryString,  TokenEntity.class).executeUpdate();
			
		}catch(Exception e){
			throw new Exception(e.getMessage());
		};
		return deletedCount;
	}
	@Transactional
	public int deleteRefreshToken(String valor) throws Exception {
		int deletedCount = 0;
		try {
			
			String queryString  = "DELETE FROM tokensso WHERE REFRESH_TOKEN =  '" + valor + "'";
			
			deletedCount = manager.createNativeQuery(queryString,  TokenEntity.class).executeUpdate();
			
		}catch(Exception e){
			throw new Exception(e.getMessage());
		};
		return deletedCount;
	}
	
	public List<TokenEntity> find(String token) throws Exception {
		List<TokenEntity> tokens = new ArrayList<TokenEntity>(0);
		try {
			
			String queryString  = "SELECT * FROM token WHERE TOKEN =" + token;
			
			tokens = manager.createNativeQuery(queryString,  TokenEntity.class)
					.getResultList();;
			
		}catch(Exception e){
			throw new Exception(e.getMessage());
		};
		return tokens;
	}
	
	public List<TokenEntity> findSSO(String valor) throws Exception {
		List<TokenEntity> tokens = new ArrayList<TokenEntity>(0);
		try {
			
			String queryString  = "SELECT * FROM tokensso WHERE ACCESS_TOKEN ='" + valor + "'";
			
			tokens = manager.createNativeQuery(queryString,  TokenEntity.class)
					.getResultList();
			
		}catch(Exception e){
			throw new Exception(e.getMessage());
		};
		return tokens;
	}
}
