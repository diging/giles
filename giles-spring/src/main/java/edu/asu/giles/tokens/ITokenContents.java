package edu.asu.giles.tokens;

public interface ITokenContents {

    public abstract String getUsername();

    public abstract void setUsername(String username);

    public abstract boolean isExpired();

    public abstract void setExpired(boolean expired);

}