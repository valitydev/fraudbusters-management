package dev.vality.fraudbusters.management.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Principal;

@Slf4j
@Service
public class UserInfoService {

    public static final String UNKNOWN = "UNKNOWN";

    public String getUserName() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication: {}", authentication);
        if (authentication == null || authentication.getPrincipal() == null) {
            return UNKNOWN;
        }
        return ((Principal) authentication.getPrincipal()).getName();
    }

    public String getUserName(Principal principal) {
        if (principal == null || !StringUtils.hasText(principal.getName())) {
            return UNKNOWN;
        }
        return principal.getName();
    }
}
