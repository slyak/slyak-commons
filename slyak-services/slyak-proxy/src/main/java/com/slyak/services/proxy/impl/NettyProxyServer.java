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

import com.slyak.services.proxy.ProxyConfig;
import com.slyak.services.proxy.ProxyServer;
import com.slyak.services.proxy.handler.ExceptionHandler;
import com.slyak.services.proxy.handler.IdleEventHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import lombok.Setter;
import lombok.SneakyThrows;
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
	private int worker = 5;

	@Setter
	private int client = 5;

	@Setter
	private int backLog = 1024;

	@Setter
	private int connectTimeout = 1000;

	@Setter
	private int port = 9999;

	@Setter
	private ProxyConfig proxyConfig;

	private NioEventLoopGroup bossGroup;

	private NioEventLoopGroup workerGroup;

	private NioEventLoopGroup clientGroup;

	boolean isTunnelMode() {
		return proxyConfig != null;
	}

	@SneakyThrows(InterruptedException.class)
	public void start() {
		ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
		ServerBootstrap bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(boss);
		workerGroup = new NioEventLoopGroup(worker);
		clientGroup = new NioEventLoopGroup(client);
		try {
			bootstrap
					.group(bossGroup, workerGroup)
					.channel(getChannelClass())
					.option(ChannelOption.SO_BACKLOG, backLog)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)

					.childOption(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_REUSEADDR, true)

					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							//channel time out handler
							pipeline.addLast(new IdleStateHandler(0, 0, 30));
							pipeline.addLast(new IdleEventHandler());
							//logging
							pipeline.addLast(new LoggingHandler());

							if (isTunnelMode()) {
								pipeline.addLast(getProxyHandler(proxyConfig));
							}
							else {
								pipeline.addLast(getCustomChannelHandlers(ch, clientGroup));
							}
							pipeline.addLast(ExceptionHandler.INSTANCE);
						}
					})
					.bind("192.168.30.176", port)
			;
			//start server
			ChannelFuture future = bootstrap.bind(port).sync();
			log.debug("Starting proxy server , port is {}", port);
			future.channel().closeFuture().sync();
		}
		finally {
			stop();
		}
	}

	@Override
	@SneakyThrows(InterruptedException.class)
	public void stop() {
		clientGroup.shutdownGracefully().sync();
		workerGroup.shutdownGracefully().sync();
		bossGroup.shutdownGracefully().sync();
	}

	abstract ChannelHandler[] getCustomChannelHandlers(SocketChannel channel, EventLoopGroup clientGroup);

	abstract ProxyHandler getProxyHandler(ProxyConfig proxyConfig);

	abstract Class<? extends ServerChannel> getChannelClass();
}
