package org.vita.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.vita.data.ProfileDao;
import org.vita.data.UserDao;
import org.vita.models.Profile;
import org.vita.models.User;

@RestController
@CrossOrigin(origins = "http://localhost:52330", allowCredentials = "true")
public class ProfileController {
    private final UserDao userDao;
    private final ProfileDao profileDao;

    @Autowired
    public ProfileController(UserDao userDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @GetMapping("/profile")
    public Profile getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.getByUserName(authentication.getName());
        int id = user.getId();

        try {
            return profileDao.getByUserId(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PutMapping("/profile")
    public Profile updateProfile(@RequestBody Profile profile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.getByUserName(authentication.getName());
        int id = user.getId();

        try {
            profile.setUserId(id);
            Profile updatedProfile = profileDao.update(profile);

            if (updatedProfile == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profile not found");
            }

            return updatedProfile;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
