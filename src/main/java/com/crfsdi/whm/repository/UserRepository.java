package com.crfsdi.whm.repository;

import java.util.List;


import com.crfsdi.whm.model.Person;

public interface UserRepository{
    Person findByUsername(String username);
    void savePersons(List<Person> persons);
    void save(Person person);
    void update(Person person);
    void delete(String username);
}