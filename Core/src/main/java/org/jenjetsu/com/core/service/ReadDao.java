package org.jenjetsu.com.core.service;

import java.util.List;

public interface ReadDao<Entity, Id> {

    public Entity findById(Id id);
    public List<Entity> findAll();

}
