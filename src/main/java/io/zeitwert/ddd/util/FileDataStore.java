package io.zeitwert.ddd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class FileDataStore {

	private final Properties properties = new Properties();

	public abstract File getFile();

	public FileDataStore() {
		if (!getFile().exists()) {
			return;
		}

		try {
			properties.load(new FileInputStream(getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getComment() {
		return "";
	}

	public String get(String key) {
		return properties.getProperty(key);
	}

	public void set(String key, String value) {
		properties.setProperty(key, value);

		try {
			properties.store(new FileOutputStream(getFile()), getComment());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}