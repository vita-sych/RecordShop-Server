package org.vita.services;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.vita.data.ProfileDao;
import org.vita.data.UserDao;
import org.vita.models.Profile;
import org.vita.models.User;
import org.vita.models.authentication.LoginDto;
import org.vita.security.jwt.TokenProvider;

@Service
public class AuthenticationService {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDao userDao;
    private final ProfileDao profileDao;

    public AuthenticationService(TokenProvider tokenProvider,
                                 AuthenticationManagerBuilder authenticationManagerBuilder,
                                 UserDao userDao,
                                 ProfileDao profileDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    public User registerUser(String username, String password, String role) throws Exception {
        if (userDao.exists(username)) {
            throw new IllegalArgumentException("User Already Exists.");
        }

        User user = userDao.create(new User(0, username, password, role));

        Profile profile = new Profile();
        profile.setUserId(user.getId());
        profileDao.create(profile);

        return user;
    }

    public String loginUser(LoginDto loginDto) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication, false);
    }

    public User getUserByUsername(String username) {
        return userDao.getByUserName(username);
    }
}