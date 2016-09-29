package edu.asu.giles.tokens;


public interface ITokenService {

    /**
     * Generate a new user token.
     * 
     * @param username
     * @return
     */
    public abstract String generateToken(String username);

    /**
     * Method to get the contents of a token. This method will simply extract the contents
     * and always return a {@link ITokenContents} object, even if the token is expired. Classes
     * using this method have to make sure a given token is not expired by calling the 
     * method <code>isExpired</code> of the returned {@link ITokenContents} object.
     * 
     * @param token The token to extract the contents from.
     * @return
     */
    public abstract ITokenContents getTokenContents(String token);

}