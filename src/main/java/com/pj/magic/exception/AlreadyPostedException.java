package com.pj.magic.exception;

public class AlreadyPostedException extends RuntimeException {

	public AlreadyPostedException(String message) {
		super(message);
	}

}