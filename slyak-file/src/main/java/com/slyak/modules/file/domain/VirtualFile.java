package com.slyak.modules.file.domain;

import java.io.File;

/**
 * Virtual File.
 *
 * @author stormning on 2017/1/4.
 */
public interface VirtualFile {
	/**
	 * get fileInfo
	 *
	 * @return
	 */
	FileInfo getFileInfo();

	/**
	 * get native file
	 *
	 * @return
	 */
	File getNativeFile();

}
