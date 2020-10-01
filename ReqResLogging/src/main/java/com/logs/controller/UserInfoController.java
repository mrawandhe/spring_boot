package com.logs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logs.model.UserInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
	
	@GetMapping("/all")
	public ResponseEntity<Object> getUsers() {
		UserInfo userInfo = UserInfo.builder().userName("username").password("pwd").email("some email").mobile("5132123").build();
		return new ResponseEntity<Object>(userInfo, null,HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Object> createUser(@RequestBody UserInfo userInfo) {
		log.debug("userInfo : "+userInfo);
		UserInfo userInfoResult = UserInfo.builder().userName("username").password("pwd").email("some email").mobile("5132123").build();
		return new ResponseEntity<Object>(userInfoResult, null,HttpStatus.OK);
	}
	
}
