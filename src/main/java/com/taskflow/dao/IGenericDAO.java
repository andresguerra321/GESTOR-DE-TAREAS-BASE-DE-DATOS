package com.taskflow.dao;

import java.util.List;

public interface IGenericDAO<T, ID> {
    List<T> getAll();
    T getById(ID id);
    boolean insert(T entity);
    boolean update(T entity);
    boolean delete(ID id);
}
