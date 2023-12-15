package com.vlaryz.bachelor.service;

import com.vlaryz.bachelor.BachelorException;
import com.vlaryz.bachelor.contract.RegisterRequest;
import com.vlaryz.bachelor.interfaces.IUserService;
import com.vlaryz.bachelor.model.Role;
import com.vlaryz.bachelor.model.User;
import com.vlaryz.bachelor.repository.IUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements IUserService, UserDetailsService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new BachelorException("user not found"));
    }

    @Override
    public void registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new BachelorException("User with this email already exist");
        }

        if (!isValidRegisterInfo(registerRequest)) {
            throw new BachelorException("Please provide full details");
        }

        User user = new User(registerRequest.getName(), registerRequest.getSurname(),
                registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()));
        user.getRoles().add(new Role("ROLE_USER"));

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (!user.isPresent())
            throw new BachelorException("Cannot find user");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.get().getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                authorities);
    }

    private boolean isValidRegisterInfo(RegisterRequest request) {
        return !request.getEmail().trim().isEmpty() && !request.getPassword().trim().isEmpty()
                && !request.getName().trim().isEmpty() && !request.getSurname().trim().isEmpty();
    }
}
