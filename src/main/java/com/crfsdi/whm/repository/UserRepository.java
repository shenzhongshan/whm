package com.crfsdi.whm.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.crfsdi.whm.model.Person;
@Mapper
public interface UserRepository extends BaseRepository<Person>{
    Person findByUsername(@Param(value = "username")String username);
    void deleteByUsername(@Param(value = "username")String username);
	List<Person> listByPage(@Param(value = "username") String username, @Param(value = "staffName")String staffName, @Param(value = "page") Long page, @Param(value = "size") Long size);
	void setAdminRole(@Param(value = "staffNo") String staffNo);
	void delAdminRole(@Param(value = "staffNo") String staffNo);
	
}