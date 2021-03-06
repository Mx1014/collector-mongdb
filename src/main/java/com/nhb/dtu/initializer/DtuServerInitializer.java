package com.nhb.dtu.initializer;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.nhb.dtu.codec.SocketDecoder;
import com.nhb.dtu.codec.SocketEncoder;
import com.nhb.dtu.config.ServerPeriod;
import com.nhb.dtu.handler.DtuServerHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

@Component
public class DtuServerInitializer extends ChannelInitializer<SocketChannel> {
	/**
	 * 线程池用于数据存取到数据库耗时业务处理
	 */
	EventExecutorGroup execGroup = new DefaultEventExecutorGroup(100);

	@Override
	protected void initChannel(SocketChannel socketChannel) {
		ChannelPipeline pipeline = socketChannel.pipeline();
		// the encoder and decoder are static as these are sharable
		pipeline.addLast(new SocketDecoder());
		pipeline.addLast(new SocketEncoder());
		pipeline.addLast(new ReadTimeoutHandler(ServerPeriod.TIMEOUT.getValue(), TimeUnit.SECONDS));
		pipeline.addLast(execGroup, new DtuServerHandler());
	}
}
