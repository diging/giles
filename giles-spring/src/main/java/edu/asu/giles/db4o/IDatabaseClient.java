package edu.asu.giles.db4o;

import edu.asu.giles.exceptions.UnstorableObjectException;


public interface IDatabaseClient<T extends IStorableObject> {

    public abstract String generateId();

    public abstract T store(T element) throws UnstorableObjectException;

}