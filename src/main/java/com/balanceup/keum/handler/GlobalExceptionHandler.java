package com.balanceup.keum.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.balanceup.keum.controller.dto.response.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> apiException(RuntimeException e) {
		return new ResponseEntity<>(
			Response.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
	}

}
