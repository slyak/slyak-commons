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

package com.slyak.services.proxy.server.impl;

import com.slyak.services.proxy.server.AuthProvider;
import com.slyak.services.proxy.server.ProxyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * .
 *
 * @author stormning 2017/4/11
 * @since 1.3.0
 */
@Slf4j
public class Socks5ProxyServer extends NettyProxyServer {

	@Override
	ChannelHandler[] getCustomChannelHandlers() {
		if (isTunnelMode()) {
			return null;
		}
		//socks auth
		//new Socks5PasswordAuthRequestDecoder();
		//new Socks5PasswordAuthRequestHandler()
		return new ChannelHandler[] {
				//Socks5MessageByteBuf
				Socks5ServerEncoder.DEFAULT,
				//socks5 init
				new Socks5InitialRequestDecoder(),
				new Socks5InitialRequestHandler(),
				//socks connection
				new Socks5CommandRequestDecoder(),
				new Socks5CommandRequestHandler()
		};
	}

	@Override
	ProxyHandler getProxyHandler(ProxyConfig proxyConfig) {
		//TODO username password
		return new Socks5ProxyHandler(
				new InetSocketAddress(proxyConfig.getProxyAddress(),
						proxyConfig.getProxyPort()));
	}

	@Override
	Class<? extends ServerChannel> getChannelClass() {
		return NioServerSocketChannel.class;
	}

	public class Socks5InitialRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialRequest msg) throws Exception {
			log.debug("init socks5 connection " + msg);
			if (msg.decoderResult().isFailure()) {
				log.debug("not a socks5 connection");
				ctx.fireChannelRead(msg);
			}
			else {
				if (msg.version().equals(SocksVersion.SOCKS5)) {
					if (false) {
						Socks5InitialResponse initialResponse = new DefaultSocks5InitialResponse(
								Socks5AuthMethod.PASSWORD);
						ctx.writeAndFlush(initialResponse);
					}
					else {
						Socks5InitialResponse initialResponse = new DefaultSocks5InitialResponse(
								Socks5AuthMethod.NO_AUTH);
						ctx.writeAndFlush(initialResponse);
					}
				}
			}
		}

	}

	public class Socks5CommandRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {

		@Override
		protected void channelRead0(final ChannelHandlerContext clientChannelContext, DefaultSocks5CommandRequest msg)
				throws Exception {
			log.debug("target server  : " + msg.type() + "," + msg.dstAddr() + "," + msg.dstPort());
			if (msg.type().equals(Socks5CommandType.CONNECT)) {
				log.trace("preparing to connect target server");
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				Bootstrap bootstrap = new Bootstrap();
				bootstrap.group(bossGroup)
						.channel(NioSocketChannel.class)
						.option(ChannelOption.TCP_NODELAY, true)
						.option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel ch) throws Exception {
								//ch.pipeline().addLast(new LoggingHandler());//in out
								//将目标服务器信息转发给客户端

								ChannelPipeline pipeline = ch.pipeline();
								pipeline.addLast(new IdleStateHandler(3, 30, 0));
								pipeline.addLast(new ProxyIdleHandler());
								//logging
								pipeline.addLast(new LoggingHandler());
								pipeline.addLast(new Dest2ClientHandler(clientChannelContext));
							}
						});
				log.trace("start connecting target server");
				ChannelFuture future = bootstrap.connect(msg.dstAddr(), msg.dstPort());
				future.addListener(new ChannelFutureListener() {

					public void operationComplete(final ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							log.trace("successfully connected to target server");
							clientChannelContext.pipeline().addLast(new Client2DestHandler(future));
							Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(
									Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
							clientChannelContext.writeAndFlush(commandResponse);
						}
						else {
							Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(
									Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
							clientChannelContext.writeAndFlush(commandResponse);
						}
					}

				});
			}
			else {
				clientChannelContext.fireChannelRead(msg);
			}
		}

		/**
		 * 将目标服务器信息转发给客户端
		 *
		 * @author huchengyi
		 */
		private class Dest2ClientHandler extends ChannelInboundHandlerAdapter {

			private ChannelHandlerContext clientChannelContext;

			public Dest2ClientHandler(ChannelHandlerContext clientChannelContext) {
				this.clientChannelContext = clientChannelContext;
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx2, Object destMsg) throws Exception {
				log.trace("将目标服务器信息转发给客户端");
				clientChannelContext.writeAndFlush(destMsg);
			}

			@Override
			public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
				log.trace("目标服务器断开连接");
				clientChannelContext.channel().close();
			}
		}

		/**
		 * 将客户端的消息转发给目标服务器端
		 *
		 * @author huchengyi
		 */
		private class Client2DestHandler extends ChannelInboundHandlerAdapter {

			private ChannelFuture destChannelFuture;

			public Client2DestHandler(ChannelFuture destChannelFuture) {
				this.destChannelFuture = destChannelFuture;
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				log.trace("将客户端的消息转发给目标服务器端");
				destChannelFuture.channel().writeAndFlush(msg);
			}

			@Override
			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				log.trace("客户端断开连接");
				destChannelFuture.channel().close();
			}
		}
	}

	public class Socks5PasswordAuthRequestHandler
			extends SimpleChannelInboundHandler<DefaultSocks5PasswordAuthRequest> {

		private AuthProvider authProvider;

		public Socks5PasswordAuthRequestHandler(AuthProvider authProvider) {
			this.authProvider = authProvider;
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5PasswordAuthRequest msg) throws Exception {
			if (authProvider.authenticate(msg.username(), msg.password())) {
//				ProxyChannelTrafficShapingHandler.username(ctx, msg.username());
				Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(
						Socks5PasswordAuthStatus.SUCCESS);
				ctx.writeAndFlush(passwordAuthResponse);
			}
			else {
//				ProxyChannelTrafficShapingHandler.username(ctx, "unauthorized");
				Socks5PasswordAuthResponse passwordAuthResponse = new DefaultSocks5PasswordAuthResponse(
						Socks5PasswordAuthStatus.FAILURE);
				//发送鉴权失败消息，完成后关闭channel
				ctx.writeAndFlush(passwordAuthResponse).addListener(ChannelFutureListener.CLOSE);
			}
		}

	}
}
