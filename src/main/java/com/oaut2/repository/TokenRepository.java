package com.oaut2.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.oaut2.entity.TokenEntity;
@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, Integer>{

}
