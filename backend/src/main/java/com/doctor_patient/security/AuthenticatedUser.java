package com.doctor_patient.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.doctor_patient.entity.User;
import com.doctor_patient.enums.Role;
import com.doctor_patient.enums.Status;

public class AuthenticatedUser implements UserDetails {

	private final User user;

	public AuthenticatedUser(User user) {
		this.user = user;
	}

	public String getUserId() {
		return user.getUserId();
	}

	public Role getRole() {
		return user.getRole();
	}

	public String getName() {
		return user.getName();
	}

	public Status getStatus() {
		return user.getStatus();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserId();
	}

	@Override
	public boolean isEnabled() {
		return user.getStatus() == null || user.getStatus() == Status.ACTIVE;
	}
}
