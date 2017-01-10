package com.slyak.modules.file.event;

import com.slyak.modules.file.domain.VirtualFile;
import org.springframework.context.ApplicationEvent;

/**
 * .
 *
 * @author stormning on 2017/1/6.
 */
public class FilePostStoreEvent extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public FilePostStoreEvent(VirtualFile source) {
		super(source);
	}
}
