package com.tim.concurrentcontrol.service;

import com.tim.concurrentcontrol.model.User;
import com.tim.concurrentcontrol.repository.UserRepository;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(User user) throws Exception {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new Exception("Username is already in use.");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new Exception("Email is already in use.");
        }

        userRepository.save(user);
        return user;
    }

    @Transactional
    public User getUser(Long id) throws Exception {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.get();
        }
        throw new Exception("User not found.");
    }

    @Transactional
    public User updateUser(Long id, String newName) throws Exception {
        try {
            User user = userRepository.findLockedById(id);

            if (user == null) {
                throw new Exception("User not found");
            }
            user.setName(newName);

            return userRepository.save(user);

        } catch (CannotAcquireLockException | LockTimeoutException | PessimisticLockException e) {
            throw new Exception("Unable to lock the record. Please try again later.");
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}