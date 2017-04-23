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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 *
 * @author stormning 2017/4/21
 * @since 1.3.0
 */
@Slf4j
public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {

	private EventLoopGroup remoteEventLoopGroup;

	public Socks5CommandRequestHandler(EventLoopGroup remoteEventLoopGroup) {
		this.remoteEventLoopGroup = remoteEventLoopGroup;
	}

	private Channel remoteChannel;

	@Override
	protected void channelRead0(final ChannelHandlerContext requestChannelContext,
			final DefaultSocks5CommandRequest msg)
			throws Exception {
		if (Socks5CommandType.CONNECT.equals(msg.type())) {
			log.debug("Start to connect remote server : {}:{}", msg.dstAddr(), msg.dstPort());
			Bootstrap bootstrap = new Bootstrap();
			bootstrap
					.group(remoteEventLoopGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new IdleStateHandler(0, 0, 30));
							pipeline.addLast(new IdleEventHandler());
							pipeline.addLast(ExceptionHandler.INSTANCE);
							pipeline.addLast(new Remote2RequestHandler(requestChannelContext.channel()));
							pipeline.addLast(ExceptionHandler.INSTANCE);
						}
					});
			final ChannelFuture future = bootstrap.connect(msg.dstAddr(), msg.dstPort());
			this.remoteChannel = future.channel();
			future.addListener(new ChannelFutureListener() {
								   @Override
								   public void operationComplete(final ChannelFuture connectFuture) throws Exception {
									   if (connectFuture.isSuccess()) {
										   log.debug("Connected to remote server");
										   requestChannelContext.pipeline().addLast(new Request2RemoteHandler(remoteChannel));
										   Socks5CommandResponse response = new DefaultSocks5CommandResponse(
												   Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
										   //add client to dest handler to receive response
										   requestChannelContext.writeAndFlush(response);
									   }
									   else {
										   log.debug("Failed to connect to remote server");
										   Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(
												   Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
										   requestChannelContext.writeAndFlush(commandResponse);
									   }
								   }
							   }
			);
		}
		else {
			log.debug("Fire channel read");
			requestChannelContext.fireChannelRead(msg);
		}
	}

	/**
	 * Send remote response msg to request channel
	 */
	private static class Remote2RequestHandler extends ChannelInboundHandlerAdapter {

		private Channel requestChannel;

		public Remote2RequestHandler(Channel requestChannel) {
			this.requestChannel = requestChannel;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object remoteResponseMsg) throws Exception {
			log.debug("Received remote msg,and write it to request channel");
			requestChannel.writeAndFlush(remoteResponseMsg);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			ctx.channel().close();
			requestChannel.close();
		}
	}

	/**
	 * Send decoded request to remote
	 */
	public class Request2RemoteHandler extends ChannelInboundHandlerAdapter {
		private final Channel remoteChannel;

		public Request2RemoteHandler(Channel remoteChannel) {
			this.remoteChannel = remoteChannel;
		}

		/**
		 * read msg from request channel,and write it to remote channel
		 *
		 * @param ctx request channel context
		 * @param msg decoded request msg
		 * @throws Exception
		 */
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			log.debug("Start to send request to remote");
			remoteChannel.writeAndFlush(msg);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			//close remote channel
			remoteChannel.close();
			//close request channel
			ctx.channel().close();
		}

	}
}
