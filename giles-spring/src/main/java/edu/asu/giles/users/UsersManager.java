package edu.asu.giles.users;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Service;

/**
 * Managing class for user management.
 * 
 * @author Julia Damerow
 * 
 */
@Service
public class UsersManager implements IUserManager {

    @Autowired
    private UserDatabaseClient client;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#findUser(java.lang.String)
     */
    @Override
    public User findUser(String name) {
        User user = client.findUser(name);
        return user;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#getUser(java.lang.String,
     * java.lang.String)
     */
    @Override
    public User getUser(String name, String pw) {
        User user = client.getUser(name, pw);
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = new User();
        user.setEmail(email);

        List<User> users = client.findUsers(user);
        if (users.size() > 0)
            return users.get(0);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#getAllUsers()
     */
    @Override
    public User[] getAllUsers() {
        return client.getAllUser();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.conceptpower.users.IUserManager#addUser(edu.asu.conceptpower.
     * users.User)
     */
    @Override
    public User addUser(User user) {
        client.addUser(user);
        return user;
    }

    @Override
    public void updatePasswordEncryption(String username) {
        User user = client.findUser(username);
        client.update(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#deleteUser(java.lang.String)
     */
    @Override
    public void deleteUser(String username) {
        client.deleteUser(username);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#storeModifiedUser(edu.asu.
     * conceptpower.users.User)
     */
    @Override
    public void storeModifiedUser(User user) {
        client.update(user);
    }

    /**
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.users.conceptpower.users.IUserManager#storeModifiedPassword(edu.asu.conceptpower.users.User)
     */
    @Override
    public void storeModifiedPassword(User user) {
        client.update(user);
    }

    @Override
    public void approveUser(String username) {
        User user = findUser(username);
        user.setAccountStatus(AccountStatus.APPROVED);
        if (!user.getRoles().contains(Roles.ROLE_USER)) {
            user.getRoles().add(Roles.ROLE_USER);
        }
        client.update(user);
    }
}