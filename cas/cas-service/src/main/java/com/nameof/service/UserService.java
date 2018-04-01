package com.nameof.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nameof.common.domain.User;

@Service
public class UserService {
	
	public User verifyUserLogin(User inputUser) {
		if (!Objects.equals(inputUser.getName(), inputUser.getPasswd())) {
			return null;
		}
		return inputUser;
	}

	public User findUserByName(String username) {
		if (StringUtils.isEmpty(username)) {
			return null;
		}
		return new User(username, username);
	}
}
