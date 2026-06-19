package com.amadeus.backend.services;

import com.amadeus.backend.dto.request.SignUpRequest;
import com.amadeus.backend.models.UserEntity;
import com.amadeus.backend.repositories.UserRepository;
import com.amadeus.backend.services.interfaces.SignUpService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class SignUpServiceImpl implements SignUpService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public String registerUser(SignUpRequest request) {
        if(!this.isStrongPassword(request.getPassword())){
            throw new IllegalArgumentException("Password is not strong");
        }

        String hash = encoder.encode(request.getPassword());

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(hash)
                .build();

        repository.save(user);

        return "Lab member registered";
    }

    private boolean isStrongPassword(String password) {
        if (password.length() < 8) return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
