package edu.asu.giles.files.impl;

import java.util.Random;

import edu.asu.giles.files.IDatabaseClient;

public abstract class DatabaseClient implements IDatabaseClient {

    /* (non-Javadoc)
     * @see edu.asu.giles.files.impl.IDatabaseClient#generateFileId()
     */
    @Override
    public String generateId() {
        String id = null;
        while(true) {
            id = getIdPrefix() + generateUniqueId();
            Object existingFile = getById(id);
            if (existingFile == null) {
                break;
            }
        }
        return id;
    }
    
    protected abstract String getIdPrefix();
    
    protected abstract Object getById(String id);
    
    /**
     * This methods generates a new 6 character long id. Note that this method
     * does not assure that the id isn't in use yet.
     * 
     * Adapted from
     * http://stackoverflow.com/questions/9543715/generating-human-readable
     * -usable-short-but-unique-ids
     * 
     * @return 6 character id
     */
    protected String generateUniqueId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                .toCharArray();

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(chars[random.nextInt(62)]);
        }

        return builder.toString();
    }
}
