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
import lombok.Builder;

/**
 * .
 *
 * @author stormning 2017/4/21
 * @since 1.3.0
 */
@Builder
public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {

	private EventLoopGroup group;

	private SocketChannel parent;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final DefaultSocks5CommandRequest msg)
			throws Exception {
		if (Socks5CommandType.CONNECT.equals(msg.type())) {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap
					.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new IdleStateHandler(0, 0, 30));
							pipeline.addLast(new IdleEventHandler());
							pipeline.addLast(new Dest2ClientHandler());
							pipeline.addLast(ExceptionHandler.INSTANCE);
						}
					});
			ChannelFuture future = bootstrap.connect(msg.dstAddr(), msg.dstPort());
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					try {
						if (future.isSuccess()) {
							Socks5CommandResponse response = new DefaultSocks5CommandResponse(
									Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
							ctx.writeAndFlush(response);

							//add client to dest handler to receive response
							ctx.pipeline().addLast(new Client2DestHandler());
						}
						else {
							Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(
									Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
							ctx.writeAndFlush(commandResponse);
						}
					}
					finally {
						future.channel().close();
						if (parent != null && parent.isActive()) {
							parent.closeFuture();
						}
					}
				}
			});
		}
	}
}
