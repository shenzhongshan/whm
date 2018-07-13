package com.crfsdi.whm.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.crfsdi.whm.model.Person;
@Mapper
public interface UserRepository extends BaseRepository<Person>{
    Person findByUsername(@Param(value = "username")String username);
    void deleteByUsername(@Param(value = "username")String username);
}