package com.slyak.modules.file.repository;

import com.slyak.modules.file.domain.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * .
 *
 * @author stormning on 2017/1/6.
 */
public interface FileRepository extends JpaRepository<FileInfo, Long> {
	List<FileInfo> findByOwner(String owner);
}
