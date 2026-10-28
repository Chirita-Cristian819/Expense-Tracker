package com.cristian.expensetracker.users;

import com.cristian.expensetracker.exceptions.EmailNotVerifiedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getUserDataByPrincipal(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserDTO.from(user);
    }

    public UserEntity getUserDataByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void checkUserIsEmailVerified(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getEmailVerifiedAt() == null){
            throw new EmailNotVerifiedException(user.getUserId());
        }
    }

}
