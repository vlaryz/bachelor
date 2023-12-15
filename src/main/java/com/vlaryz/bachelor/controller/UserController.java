package com.vlaryz.bachelor.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vlaryz.bachelor.contract.ErrorResponse;
import com.vlaryz.bachelor.contract.LoginRequest;
import com.vlaryz.bachelor.contract.LoginResponse;
import com.vlaryz.bachelor.contract.RegisterRequest;
import com.vlaryz.bachelor.interfaces.IUserService;
import com.vlaryz.bachelor.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> getAuthenticationToken(@RequestBody LoginRequest loginRequest, HttpServletRequest request,
                                                    HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
        var x = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)x.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1 * 60 * 100000))   //1000min
                .withIssuer("Judesys")
                .withClaim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 100000))   //1000min
                .withIssuer("Judesys")
                .sign(algorithm);
//        response.setHeader("access_token", access_token);
//        response.setHeader("refresh_token", refresh_token);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody RegisterRequest registerRequest)  {
        userService.registerUser(registerRequest);

        return new ResponseEntity(HttpStatus.OK);
    }

}
