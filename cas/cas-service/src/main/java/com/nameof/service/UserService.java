package com.nameof.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.nameof.common.domain.User;

@Service
public class UserService {
	
	public User verifyUserLogin(User inputUser) {
		if (!Objects.equals(inputUser.getName(), inputUser.getPasswd())) {
			return null;
		}
		return inputUser;
	}
}
