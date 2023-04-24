package org.jenjetsu.com.core.service;

public interface DeleteDao<Entity, Id> {

    public boolean delete(Entity entity);
    public boolean deleteById(Id id);
}
