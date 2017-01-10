package com.slyak.modules.file.service;

import com.slyak.modules.file.domain.FileInfo;
import com.slyak.modules.file.domain.VirtualFile;
import com.slyak.modules.file.event.FilePostStoreEvent;
import com.slyak.modules.file.event.FilePreStoreEvent;
import com.slyak.modules.file.repository.FileRepository;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * .
 *
 * @author stormning on 2017/1/6.
 */
public class DefaultFileService implements FileService, ApplicationEventPublisherAware {

	@Setter
	private FileRepository fileRepository;

	private ApplicationEventPublisher applicationEventPublisher;

	@Setter
	private StorageService storageService;

	@Override
	public FileInfo store(FileInfo fileInfo) {
		applicationEventPublisher.publishEvent(new FilePreStoreEvent(fileInfo));
		fileInfo.setKey(storageService.store(fileInfo.getInputStream()));
		fileRepository.save(fileInfo);
		applicationEventPublisher.publishEvent(new FilePostStoreEvent(toVirtualFile(fileInfo)));
		return fileInfo;
	}

	@Override
	public VirtualFile toVirtualFile(FileInfo fileInfo) {
		return new DefaultVirtualFile(fileInfo);
	}

	@Override
	public VirtualFile toVirtualFile(Long id) {
		return toVirtualFile(fileRepository.findOne(id));
	}

	@Override
	public FileInfo getOwnerFile(String owner) {
		List<FileInfo> infos = getOwnerFiles(owner);
		if (CollectionUtils.isEmpty(infos)) {
			return null;
		}
		return infos.get(0);
	}

	@Override
	public List<FileInfo> getOwnerFiles(String owner) {
		return fileRepository.findByOwner(owner);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	class DefaultVirtualFile implements VirtualFile {

		private FileInfo fileInfo;

		public DefaultVirtualFile(FileInfo fileInfo) {
			this.fileInfo = fileInfo;
		}

		@Override
		public FileInfo getFileInfo() {
			return this.fileInfo;
		}

		@Override
		public File getNativeFile() {
			return storageService.restore(fileInfo.getKey());
		}
	}

}
