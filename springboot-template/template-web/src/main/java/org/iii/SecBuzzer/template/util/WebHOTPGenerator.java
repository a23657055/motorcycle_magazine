package org.iii.SecBuzzer.template.util;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;

/**
 * <p>
 * Generates HMAC-based one-time passwords (HOTP) as specified in
 * <a href="https://tools.ietf.org/html/rfc4226">RFC&nbsp;4226</a>.
 * </p>
 *
 * <p>
 * {@code HmacOneTimePasswordGenerator} instances are thread-safe and may be
 * shared and re-used across multiple threads.
 * </p>
 */
public class WebHOTPGenerator {
	private final String algorithm;
	private final int codeLength;

	private final int modDivisor;

	/**
	 * The default length, in decimal digits, for one-time passwords.
	 */
	public static final int DEFAULT_PASSWORD_LENGTH = 6;

	/**
	 * The HMAC algorithm specified by the HOTP standard.
	 */
	public static final String HOTP_HMAC_ALGORITHM = "HmacSHA1";

	/**
	 * Creates a new HMAC-based one-time password (HOTP) generator using a
	 * default password length
	 * ({@value tw.gov.ndc.ndcdashboard.web.WebHOTPGenerator#DEFAULT_PASSWORD_LENGTH}
	 * digits).
	 *
	 * @throws NoSuchAlgorithmException
	 *             if the underlying JRE doesn't support the
	 *             {@value tw.gov.ndc.ndcdashboard.web.WebHOTPGenerator#HOTP_HMAC_ALGORITHM}
	 *             algorithm, which should never happen except in cases of
	 *             serious misconfiguration
	 */
	public WebHOTPGenerator() throws NoSuchAlgorithmException {
		this(DEFAULT_PASSWORD_LENGTH);
	}

	/**
	 * Creates a new HMAC-based one-time password (HOTP) generator using the
	 * given password length.
	 *
	 * @param codeLength
	 *            the length, in decimal digits, of the one-time passwords to be
	 *            generated; must be between 6 and 8, inclusive
	 *
	 * @throws NoSuchAlgorithmException
	 *             if the underlying JRE doesn't support the
	 *             {@value tw.gov.ndc.ndcdashboard.web.WebHOTPGenerator#HOTP_HMAC_ALGORITHM}
	 *             algorithm, which should never happen except in cases of
	 *             serious misconfiguration
	 */
	public WebHOTPGenerator(final int codeLength) throws NoSuchAlgorithmException {
		this(codeLength, HOTP_HMAC_ALGORITHM);
	}

	/**
	 * <p>
	 * Creates a new HMAC-based one-time password generator using the given
	 * password length and algorithm. Note that
	 * <a href="https://tools.ietf.org/html/rfc4226">RFC&nbsp;4226</a> specifies
	 * that HOTP must always use HMAC-SHA1 as an algorithm, but derived one-time
	 * password systems like TOTP may allow for other algorithms.
	 * </p>
	 *
	 * @param codeLength
	 *            the length, in decimal digits, of the one-time passwords to be
	 *            generated; must be between 6 and 8, inclusive
	 * @param algorithm
	 *            the name of the {@link javax.crypto.Mac} algorithm to use when
	 *            generating passwords; note that HOTP only allows for
	 *            {@value tw.gov.ndc.ndcdashboard.web.WebHOTPGenerator#HOTP_HMAC_ALGORITHM},
	 *            but derived standards like TOTP may allow for other algorithms
	 *
	 * @throws NoSuchAlgorithmException
	 *             if the given algorithm is not supported by the underlying JRE
	 */
	protected WebHOTPGenerator(final int codeLength, final String algorithm) throws NoSuchAlgorithmException {
		switch (codeLength) {
			case 6 : {
				this.modDivisor = 1_000_000;
				break;
			}

			case 7 : {
				this.modDivisor = 10_000_000;
				break;
			}

			case 8 : {
				this.modDivisor = 100_000_000;
				break;
			}

			default : {
				throw new IllegalArgumentException("Password length must be between 6 and 8 digits.");
			}
		}

		this.codeLength = codeLength;

		// Our purpose here is just to throw an exception immediately if the
		// algorithm is bogus.
		Mac.getInstance(algorithm);
		this.algorithm = algorithm;
	}

	/**
	 * Generates a one-time password using the given key and counter value.
	 *
	 * @param key
	 *            a secret key to be used to generate the password
	 * @param counter
	 *            the counter value to be used to generate the password
	 *
	 * @return an integer representation of a one-time password; callers will
	 *         need to format the password for display on their own
	 *
	 * @throws InvalidKeyException
	 *             if the given key is inappropriate for initializing the
	 *             {@link Mac} for this generator
	 */
	public int generateOneTimePassword(final Key key, final long counter) throws InvalidKeyException {
		final Mac mac;

		try {
			mac = Mac.getInstance(this.algorithm);
			mac.init(key);
		} catch (final NoSuchAlgorithmException e) {
			// This should never happen since we verify that the algorithm is
			// legit in the constructor.
			throw new RuntimeException(e);
		}

		final ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, counter);

		final byte[] hmac = mac.doFinal(buffer.array());
		final int offset = hmac[hmac.length - 1] & 0x0f;

		for (int i = 0; i < 4; i++) {
			// Note that we're re-using the first four bytes of the buffer here;
			// we just ignore the latter four from
			// here on out.
			buffer.put(i, hmac[i + offset]);
		}

		final int hotp = buffer.getInt(0) & 0x7fffffff;

		return hotp % this.modDivisor;
	}

	/**
	 * Returns the length, in decimal digits, of passwords produced by this
	 * generator.
	 *
	 * @return the length, in decimal digits, of passwords produced by this
	 *         generator
	 */
	public int getCodeLength() {
		return this.codeLength;
	}

	/**
	 * Returns the name of the HMAC algorithm used by this generator.
	 *
	 * @return the name of the HMAC algorithm used by this generator
	 */
	public String getAlgorithm() {
		return this.algorithm;
	}
}