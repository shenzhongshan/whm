package com.crfsdi.whm.repository;


import com.crfsdi.whm.model.person;

public interface UserRepository{
    person findByUsername(String username);
}