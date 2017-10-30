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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5InitialResponse;

/**
 * .
 *
 * @author stormning 2017/4/21
 * @since 1.3.0
 */
public class Socks5InitialRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {

	private boolean needAuth = false;

	public boolean isNeedAuth() {
		return needAuth;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialRequest msg) throws Exception {
		if (msg.decoderResult().isFailure()) {
			ctx.fireChannelRead(msg);
		}
		else {
			if (SocksVersion.SOCKS5.equals(msg.version())) {
				if (isNeedAuth()) {
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
