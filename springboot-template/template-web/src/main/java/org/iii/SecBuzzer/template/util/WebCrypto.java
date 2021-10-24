package org.iii.SecBuzzer.template.util;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全性工具
 */
public class WebCrypto {

	final static Logger logger = LoggerFactory.getLogger(WebCrypto.class);
	private static SecureRandom random = new SecureRandom();
	// final static String CHARSET = "UTF-8";
	final static String ALPHA_CAPS = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // -O -I
	final static String ALPHA = "abcdefghijkmnopqrstuvwxyz"; // -l
	final static String NUMERIC = "23456789"; // -0 -1
	final static String SPECIAL_CHARS = "!@#$%^&*_=+-/";
	final static int DEFAULT_PASSWORD_LENGTH = 8;

	/**
	 * HashType
	 */
	public enum HashType {
		/**
		 * Using MD5
		 */
		MD5("MD5"),
		/**
		 * Using SHA-1
		 */
		SHA1("SHA-1"),
		/**
		 * Using SHA-256
		 */
		SHA256("SHA-256"),
		/**
		 * Using SHA-512
		 */
		SHA512("SHA-512");

		private String value;

		private HashType(String value) {
			this.value = value;
		}

		/**
		 * 取得Hash Type
		 * 
		 * @return Hash Type
		 */
		public String getValue() {
			return this.value;
		}
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
	/**
	 * 產生不包含連結符號的UUID
	 * 
	 * @return UUID without hyphen
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	/**
	 * 產生Hash Code
	 * 
	 * @param hashType
	 *            HashType
	 * @param text
	 *            原文
	 * @return 雜湊值
	 */
	public static String getHash(HashType hashType, String text) {
		MessageDigest md = null;
		byte[] hashValue = new byte[32];
		try {
			md = MessageDigest.getInstance(hashType.getValue());
			md.update(text.getBytes(StandardCharsets.UTF_8.toString()), 0, text.length());
			hashValue = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return convertToHex(hashValue);
	}
	
	/**
	 * 產生Hash Code
	 * 
	 * @param hashType HashType
	 * @param bytes    byte[]
	 * @return 雜湊值
	 */
	public static String getHash(HashType hashType, byte[] bytes) {
		MessageDigest md = null;
		byte[] hashValue = new byte[32];
		try {
			md = MessageDigest.getInstance(hashType.getValue());
			hashValue = md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return convertToHex(hashValue);
	}
	
	/**
	 * 產生隨機密碼
	 * 
	 * @return 隨機密碼(預設長度8)
	 */
	public static String getRandomPassword() {
		return getRandomPassword(DEFAULT_PASSWORD_LENGTH);
	}
	/**
	 * 產生隨機密碼
	 * 
	 * @param codeLength
	 *            密碼長度
	 * @return 隨機密碼
	 */
	public static String getRandomPassword(int codeLength) {
		String result = "";
		try {
			String dic = ALPHA_CAPS + ALPHA + NUMERIC + SPECIAL_CHARS;
			for (int i = 0; i < codeLength; i++) {
				int index = random.nextInt(dic.length());
				result += dic.charAt(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * 產生隨機碼(不包含特殊字元)
	 * 
	 * @param codeLength
	 *            隨機碼長度
	 * @return 隨機碼
	 */
	public static String getRandomCode(int codeLength) {
		String result = "";
		try {
			String dic = ALPHA_CAPS + ALPHA + NUMERIC;
			for (int i = 0; i < codeLength; i++) {
				int index = random.nextInt(dic.length());
				result += dic.charAt(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}

	/**
	 * AES加密
	 * 
	 * @param keyString
	 *            金鑰
	 * @param ivString
	 *            初始值
	 * @param plainText
	 *            明文
	 * @return 密文
	 */
	public static String aesEncrypt(String keyString, String ivString, String plainText) {
		try {
			IvParameterSpec iv = new IvParameterSpec(ivString.getBytes(StandardCharsets.UTF_8.toString()));
			SecretKeySpec skeySpec = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8.toString()), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(plainText.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * AES解密
	 * 
	 * @param keyString
	 *            金鑰
	 * @param ivString
	 *            初始值
	 * @param cipherText
	 *            密文
	 * @return 明文
	 */
	public static String aesDecrypt(String keyString, String ivString, String cipherText) {
		try {
			IvParameterSpec iv = new IvParameterSpec(ivString.getBytes(StandardCharsets.UTF_8.toString()));
			SecretKeySpec skeySpec = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8.toString()), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.decodeBase64(cipherText));

			return new String(original);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
}
