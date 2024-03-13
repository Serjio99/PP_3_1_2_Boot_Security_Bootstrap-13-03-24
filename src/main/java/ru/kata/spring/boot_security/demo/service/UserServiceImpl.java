package ru.kata.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.security.SecurityUserDetails;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements ru.kata.spring.boot_security.demo.service.UserService, UserDetailsService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(ru.kata.spring.boot_security.demo.dao.UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findOne(int id) {
        return userDao.findById(id).orElseThrow();
    }

    public User findOne() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecurityUserDetails) authentication.getPrincipal()).getUser();
    }

    @Transactional(readOnly = true)
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Transactional(readOnly = true)
    public void update(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Transactional(readOnly = true)
    public void delete(int id) {
        userDao.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userDao.findByName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Такого пользователя не существует");
        }
        return new SecurityUserDetails(user.get());
    }
}
