package org.jenjetsu.com.core.service;

import java.util.Collection;

public interface CreateDao<Entity, Id> {

    public void create(Entity entity);
    public void createAll(Collection<Entity> entities);

}
