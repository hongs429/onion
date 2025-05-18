package com.onion.backend.common.util;


import com.onion.backend.user.domain.UserDetailsImpl;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static UUID getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUserId();
        }
        return null;
    }
}
