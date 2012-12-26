/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package example.echo.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler implementation for the echo client. It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server on activation.
 */
public class EchoClientHandler extends ChannelInboundByteHandlerAdapter {

	private static final Logger log = LoggerFactory
			.getLogger(EchoClientHandler.class.getName());

	private final ByteBuf message;

	public EchoClientHandler(final int messageSize) {

		message = Unpooled.buffer(messageSize);

		for (int i = 0; i < message.capacity(); i++) {
			message.writeByte((byte) i);
		}

	}

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {

		log.info("ECHO active {}", this);

		ctx.write(message);

	}

	private volatile long count;

	@Override
	public void inboundBufferUpdated(final ChannelHandlerContext ctx,
			final ByteBuf in) {

		final ByteBuf out = ctx.nextOutboundByteBuffer();

		out.discardReadBytes();

		out.writeBytes(in);

		ctx.flush();

		if (count % 1000 == 0) {
			log.info("count {}", count);
		}

		count++;

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx,
			final Throwable cause) {

		log.error("close the connection when an exception is raised", cause);

		ctx.close();

	}

}
