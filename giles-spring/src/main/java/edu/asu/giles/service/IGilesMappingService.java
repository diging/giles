package edu.asu.giles.service;

import edu.asu.giles.exceptions.GilesMappingException;

public interface IGilesMappingService<T1, T2> {

    /**
     * Method that maps an object of T1 onto an object of type T2.
     * This method expects that the fields in T1 and T2 that should be
     * mapped onto each other have the same name and type.
     * 
     * @param t1 The object to be mapped onto another one.
     * @param t2 The object that should contain all the values of the first object.
     * @return t2 containing all the values of t1.
     * @throws GilesMappingException
     */
    public abstract T2 convert(T1 t1, T2 t2) throws GilesMappingException;

}