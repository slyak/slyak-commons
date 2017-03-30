package com.slyak.modules.file.service;

import java.io.File;
import java.io.InputStream;

/**
 * .
 *
 * @author stormning on 2017/1/6.
 */
public interface StorageService {
	/**
	 * @param inputStream
	 * @return store key
	 */
	String store(InputStream inputStream);

	File restore(String key);
}
