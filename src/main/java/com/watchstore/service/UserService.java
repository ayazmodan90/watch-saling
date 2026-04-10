package com.watchstore.service;

import com.watchstore.model.User;
import com.watchstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User registerUser(User user) {
        user.setRole(User.Role.CUSTOMER);
        return userRepository.save(user);
    }

    public void updateUserStatus(Long id, User.Status status) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(status);
            userRepository.save(user);
        });
    }

    public void updateUserRole(Long id, User.Role role) {
        userRepository.findById(id).ifPresent(user -> {
            user.setRole(role);
            userRepository.save(user);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public long countCustomers() {
        return userRepository.countByRole(User.Role.CUSTOMER);
    }

    public long countSellers() {
        return userRepository.countByRole(User.Role.SELLER);
    }

    // Simple login check (no Spring Security)
    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password) && user.getStatus() == User.Status.ACTIVE) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
