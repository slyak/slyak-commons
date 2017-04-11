package com.slyak.modules.file.service;

import java.io.File;
import java.io.InputStream;

/**
 * .
 *
 * @author stormning on 2017/1/6.
 */
public class FileSystemStorage implements StorageService {

	private String storePath;

	public static void main(String[] args) {
	}

	@Override
	public String store(InputStream inputStream) {
		String key = "";

		return key;
	}

	@Override
	public File restore(String key) {
		return null;
	}
}
