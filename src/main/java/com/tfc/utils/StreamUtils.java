package com.tfc.utils;

import java.io.*;
import java.util.Objects;
import java.util.function.Supplier;

public class StreamUtils {
	public static String readFromCLSilently(String path) {
		try {
			return readFromCL(path);
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public static String readFromCL(String path) throws IOException {
		return read(Objects.requireNonNull(StreamUtils.class.getClassLoader().getResourceAsStream(path)));
	}
	
	public static String read(InputStream stream) throws IOException {
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();
		return new String(bytes);
	}
	
	public static String readFileOrCreateAndDefault(String file, Supplier<String> defaultVal) {
		try {
			File f = new File(Arguments.isDevEnvro ? ("run/" + file) : file);
			if (!f.exists() && (Arguments.extractProgram || file.equals("credits.txt"))) {
				try {
					f.getParentFile().mkdirs();
				} catch (Throwable ignored) {
				}
				f.createNewFile();
				FileOutputStream stream = new FileOutputStream(f);
				String def = defaultVal.get();
				stream.write(def.getBytes());
				stream.close();
				return def;
			} else {
				String returnVal = readFile(file);
				return returnVal == null ? defaultVal.get() : returnVal;
			}
		} catch (Throwable ignored) {
			return defaultVal.get();
		}
	}
	
	public static String readFile(String file) {
		try {
			file = (Arguments.isDevEnvro ? ("run/" + file) : file);
			FileInputStream stream = new FileInputStream(new File(file));
			byte[] bytes = new byte[stream.available()];
			stream.read(bytes);
			stream.close();
			return new String(bytes);
		} catch (Throwable ignored) {
			return null;
		}
	}
}
