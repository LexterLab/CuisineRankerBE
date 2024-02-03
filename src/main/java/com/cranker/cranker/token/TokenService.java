package com.cranker.cranker.token;

import com.cranker.cranker.user.User;

public interface TokenService {
     String generateToken(User user);
     void confirmToken(String value);

    default User getUserByToken(String value){
        return new User();
    }
}
