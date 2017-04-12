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

package com.slyak.services.proxy.impl;

import com.slyak.services.proxy.ProxyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/4/10
 * @since 1.3.0
 */
@Slf4j
public abstract class NettyProxyServer implements ProxyServer {

	@Setter
	private int boss = 1;

	@Setter
	private int worker = 6;

	@Setter
	private int backLog = 1024;

	@Setter
	private int connectTimeout = 1000;

	@Setter
	private int port = 8088;

	private ChannelHandler[] defaultChannelHandlers = new ChannelHandler[] {
			//channel time out handler
			new IdleStateHandler(3, 30, 0),
			new ProxyIdleHandler(),
			//logging
			new LoggingHandler()
	};

	@Override
	public void start() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(boss);
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(worker);
		try {
			bootstrap
					.group(bossGroup, workerGroup)
					.channel(getChannelClass())
					.option(ChannelOption.SO_BACKLOG, backLog)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(defaultChannelHandlers);
							ChannelHandler[] customChannelHandlers = getCustomChannelHandlers();
							if (customChannelHandlers != null && customChannelHandlers.length > 0) {
								pipeline.addLast(customChannelHandlers);
							}
						}
					})
					.bind(port)
			;
			//start server
			ChannelFuture future = bootstrap.bind(port).sync();
			log.debug("Starting proxy server , port is {}", port);
			future.channel().closeFuture().sync();
		}
		catch (InterruptedException e) {
			log.error("An error occurred when starting netty proxy server.", e);
		}
		finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@Override
	public void stop() {
	}

	abstract ChannelHandler[] getCustomChannelHandlers();

	abstract Class<? extends ServerChannel> getChannelClass();

	public static class ProxyIdleHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (evt instanceof IdleStateEvent) {
				ctx.channel().close();
			}
			else {
				super.userEventTriggered(ctx, evt);
			}
		}
	}

}
