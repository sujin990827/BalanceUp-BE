package com.balanceup.keum.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {

	private String resultCode;
	private String message;
	private T body;

	public static Response<Object> of(String resultCode, String message, Object body) {
		return new Response<>(resultCode, message, body);
	}

	public static Response<Object> of(String resultCode, String message) {
		return new Response<>(resultCode, message, null);
	}

}
