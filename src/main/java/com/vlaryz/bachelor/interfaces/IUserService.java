package com.vlaryz.bachelor.interfaces;

import com.vlaryz.bachelor.contract.LoginRequest;
import com.vlaryz.bachelor.contract.LoginResponse;
import com.vlaryz.bachelor.contract.RegisterRequest;
import com.vlaryz.bachelor.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUserService {
    User getUserByEmail(String email);
    void registerUser(RegisterRequest registerRequest);
    UserDetails loadUserByUsername(String username);
}
