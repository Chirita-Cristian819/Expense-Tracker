package com.cristian.expensetracker.users;

import com.cristian.expensetracker.exceptions.EmailNotVerifiedException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO getUserDataByPrincipal(String email){
        UserEntity user = userRepository.findByEmail(email);
        return UserDTO.from(user);
    }

    public void checkUserIsEmailVerified(String email){
        UserEntity user = userRepository.findByEmail(email);
        if(user.getEmailVerifiedAt() == null){
            throw new EmailNotVerifiedException(user.getUserId());
        }
    }
}
