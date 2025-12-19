package org.vita.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.vita.data.ProfileDao;
import org.vita.data.UserDao;
import org.vita.models.Profile;
import org.vita.models.User;

@Service
public class ProfileService {

    private final UserDao userDao;
    private final ProfileDao profileDao;

    @Autowired
    public ProfileService(UserDao userDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userDao.getByUserName(authentication.getName());
    }

    public Profile getProfile() {
        User user = getCurrentUser();
        return profileDao.getByUserId(user.getId());
    }

    public Profile updateProfile(Profile profile) {
        User user = getCurrentUser();
        profile.setUserId(user.getId());
        return profileDao.update(profile);
    }
}
