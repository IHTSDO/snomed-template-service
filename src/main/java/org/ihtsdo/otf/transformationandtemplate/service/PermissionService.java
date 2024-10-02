package org.ihtsdo.otf.transformationandtemplate.service;

import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClient;
import org.ihtsdo.otf.transformationandtemplate.service.client.SnowstormClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PermissionService {

    private final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    public static final String GLOBAL_ROLE_SCOPE = "global";
    private static final String BRANCH_MAIN = "MAIN";

    @Autowired
    private SnowstormClientFactory snowstormClientFactory;

    public boolean userHasRoleOnBranch(String role, String branchPath, Authentication authentication) {
        Set<String> userRoleForBranch;
        if (GLOBAL_ROLE_SCOPE.equals(branchPath)) {
            userRoleForBranch = getTSClient().getBranch(BRANCH_MAIN).getGlobalUserRoles();
        } else {
            userRoleForBranch = getTSClient().getBranch(branchPath).getUserRoles();
        }
        boolean contains = userRoleForBranch.contains(role);
        if (!contains) {
            String username = getUsername(authentication);
            logger.info("User '{}' does not have required role '{}' on branch '{}', on this branch they have roles:{}.", username, role, branchPath, userRoleForBranch);
        }
        return contains;
    }

    private SnowstormClient getTSClient() {
        return snowstormClientFactory.getClientForCurrentUser();
    }

    private String getUsername(Authentication authentication) {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null) {
                return principal.toString();
            }
        }
        return null;
    }

}
