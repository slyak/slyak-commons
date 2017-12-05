/*
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.slyak.services.file.web;

import com.slyak.services.file.domain.FileInfo;
import com.slyak.services.file.service.FileService;
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
