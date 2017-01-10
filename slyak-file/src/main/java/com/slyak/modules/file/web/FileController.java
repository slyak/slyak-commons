package com.slyak.modules.file.web;

import com.slyak.modules.file.domain.FileInfo;
import com.slyak.modules.file.service.FileService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * .
 *
 * @author stormning on 2017/1/5.
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

	private FileService fileService;

	@RequestMapping("/upload")
	@SneakyThrows
	public FileInfo upload(MultipartFile file, String owner) {
		FileInfo fileInfo = new FileInfo(file);
		fileInfo.setOwner(owner);
		/*
		file maybe in
		an album
		belongs to a category
		belongs to an user
		so ... it may has many owners(tags)
		so we store just one owner(main tag)
		and we publish an StoreEvent for further usage
		*/
		return fileService.store(fileInfo);
	}

	@RequestMapping("/download/{fileIdOrOwnerId}")
	public FileInfo download(@PathVariable(name = "fileIdOrOwnerId") String fileIdOrOwnerId) {
//		VirtualFile virtualFile = fileService.toVirtualFile(id);
		return null;
	}
}
