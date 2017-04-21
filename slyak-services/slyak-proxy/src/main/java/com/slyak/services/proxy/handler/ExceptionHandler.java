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

package com.slyak.services.proxy.handler;

import io.netty.channel.*;

/**
 * .
 *
 * @author stormning 2017/4/13
 * @since 1.3.0
 */
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelDuplexHandler {
	private ExceptionHandler() {
	}

	public static final ExceptionHandler INSTANCE = new ExceptionHandler();

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	/*@Override
	public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
			ChannelPromise promise) throws Exception {
		ctx.connect(remoteAddress, localAddress, promise.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess()) {
					// Handle connect exception here...
					future.channel().closeFuture();
				}
			}
		}));
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		ctx.write(msg, promise.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess()) {
					// Handle write exception here...
					future.channel().closeFuture();
				}
			}
		}));
	}*/

	// ... override more outbound methods to handle their exceptions as well
}
