package com.oaut2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oaut2.entity.UserTempEntity;

@Repository
public interface UserTempRepository extends JpaRepository<UserTempEntity, String>{

	
}
