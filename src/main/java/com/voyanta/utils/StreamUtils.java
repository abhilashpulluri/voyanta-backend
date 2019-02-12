package com.voyanta.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public final class StreamUtils {

	public static final String UTF8 = "UTF-8";

	private StreamUtils() {
	}

	public static BufferedReader getBufferedReader(final InputStream input, final String encoding) throws UnsupportedEncodingException {
		if (encoding == null || !Charset.isSupported(encoding)) {
			return new BufferedReader(new InputStreamReader(input));
		}

		return new BufferedReader(new InputStreamReader(input, encoding));
	}

}
