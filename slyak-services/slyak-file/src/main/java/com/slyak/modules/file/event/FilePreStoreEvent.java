package com.slyak.modules.file.event;

import com.slyak.modules.file.domain.FileInfo;
import org.springframework.context.ApplicationEvent;

/**
 * .
 *
 * @author stormning on 2017/1/6.
 */
public class FilePreStoreEvent extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public FilePreStoreEvent(FileInfo source) {
		super(source);
	}
}
