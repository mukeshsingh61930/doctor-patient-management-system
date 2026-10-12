package com.doctor_patient.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import com.doctor_patient.enums.Role;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static AuthenticatedUser currentUser(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
		}
		return user;
	}

	public static boolean hasRole(Authentication authentication, Role role) {
		return authentication != null
				&& authentication.getAuthorities().stream()
						.anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.name()));
	}

	public static void requireOwnerOrRole(Authentication authentication, String requestedUserId, Role... allowedRoles) {
		AuthenticatedUser user = currentUser(authentication);
		for (Role allowedRole : allowedRoles) {
			if (hasRole(authentication, allowedRole)) {
				return;
			}
		}
		if (!user.getUserId().equals(requestedUserId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can access only your own records");
		}
	}
}
