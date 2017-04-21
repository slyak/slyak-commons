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
import com.slyak.services.proxy.handler.Socks5CommandRequestHandler;
import com.slyak.services.proxy.handler.Socks5InitialRequestHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
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
	ChannelHandler[] getCustomChannelHandlers(SocketChannel channel, EventLoopGroup clientGroup) {
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
				Socks5CommandRequestHandler.builder().parent(channel).group(clientGroup).build()
		};
	}

	@Override
	ProxyHandler getProxyHandler(ProxyConfig proxyConfig) {
		return new Socks5ProxyHandler(
				new InetSocketAddress(proxyConfig.getProxyAddress(),
						proxyConfig.getProxyPort()));
	}

	@Override
	Class<? extends ServerChannel> getChannelClass() {
		return NioServerSocketChannel.class;
	}

}
