package util;

import java.math.BigInteger;
import java.security.SecureRandom;

import play.Play;
import play.libs.Crypto;

public class CryptoUtil {
	private static SecureRandom xRandom = new SecureRandom();
	public static String sign(String randomString) {
		return Crypto.sign(randomString, Play.secretKey.getBytes());
	}
	public static String randomstr(int size) {
		String randomString = new BigInteger(size, xRandom).toString(32);
		return randomString;
	}
}
