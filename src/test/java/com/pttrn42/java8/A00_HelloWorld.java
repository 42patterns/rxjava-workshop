package com.pttrn42.java8;

import lombok.var;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertTrue;

public class A00_HelloWorld {

	private MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

	public A00_HelloWorld() throws NoSuchAlgorithmException { }

	/**
	 * TODO The password is the name of this test
	 */
	@Test
	public void pleaseReadTheJavaDoc() throws Exception {
		//given
		var pwd = "what's the password?";
//		var pwd = "pleaseReadTheJavaDoc";

		//when
		final byte[] hash = sha256.digest(pwd.getBytes(UTF_8));

		//then
		assertTrue(Arrays.equals(hash, new byte[] {
				24, -106, 92, 124, -16, 116, -59, -106, 99, -105, 81, -111, 89, -114, -37, -125, 88, -108, -96, -76, 66, 90, -44, -15, 35, 30, -115, -90, -60, -89, 56, -116
		}));
	}
}
