package org.iii.SecBuzzer.template.util;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class File {
	final static Logger logger = LoggerFactory.getLogger(File.class);

	public static InputStream readFileFromResource(String fileName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			return classLoader.getResourceAsStream(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
	}
}
