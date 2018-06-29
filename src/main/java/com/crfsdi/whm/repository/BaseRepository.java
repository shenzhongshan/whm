package com.crfsdi.whm.repository;

import java.util.List;

public interface BaseRepository<T>{
	T load(Long id);
    void saveMore(List<T> objs);
    void save(T obj);
    void update(T obj);
    void delete(Long id);
}