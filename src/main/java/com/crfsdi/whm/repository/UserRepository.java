package com.crfsdi.whm.repository;

import com.crfsdi.whm.model.Person;

public interface UserRepository extends BaseRepository<Person>{
    Person findByUsername(String username);
    void deleteByUsername(String username);
}