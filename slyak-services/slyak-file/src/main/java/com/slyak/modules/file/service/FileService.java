package com.slyak.modules.file.service;

import com.slyak.modules.file.domain.FileInfo;
import com.slyak.modules.file.domain.VirtualFile;

import java.util.List;

/**
 * .
 *
 * @author stormning on 2017/1/4.
 */
public interface FileService {
	FileInfo store(FileInfo fileInfo);

	VirtualFile toVirtualFile(FileInfo fileInfo);

	VirtualFile toVirtualFile(Long id);

	FileInfo getOwnerFile(String owner);

	List<FileInfo> getOwnerFiles(String owner);
}
