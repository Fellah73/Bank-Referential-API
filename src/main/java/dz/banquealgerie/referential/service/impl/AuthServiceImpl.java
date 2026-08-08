package dz.banquealgerie.referential.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import dz.banquealgerie.referential.domain.entities.UserEntity;
import dz.banquealgerie.referential.repositories.auth.AuthRepository;
import dz.banquealgerie.referential.service.AuthService;
import dz.banquealgerie.referential.service.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    // login method
    @Override
    public String[] login(UserEntity userEntity) {
        UserEntity userRecord = userRepository.findAllByEmail(userEntity.getEmail());
        if (userRecord == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(userEntity.getPassword(), userRecord.getPassword())) {
            throw new RuntimeException("Wrong password");
        }
 
        String[] response = new String[2];
        response[0] = jwtService.generateToken(userRecord.getUsername(), userRecord.getEmail());
        response[1] = userRecord.getRole().name();

        return response;
    }

    // me method
    @Override
    public UserEntity getMyInfo(String email) {
        UserEntity user = userRepository.findAllByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setPassword(null);
        return user;
    }
}
