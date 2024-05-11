package com.conaxgames.entity.wrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.v1_8_R3.*;

import javax.crypto.SecretKey;
import java.net.SocketAddress;

public class NetworkManagerWrapper extends NetworkManager {

	private IChatBaseComponent disconnectReason;

	public NetworkManagerWrapper() {
		super(EnumProtocolDirection.SERVERBOUND);
	}

	@Override
	public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {

	}

	@Override
	public void a(EnumProtocol enumprotocol) {

	}

	@Override
	public void channelInactive(ChannelHandlerContext channelhandlercontext) {

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {

	}

	@Override
	protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) {

	}

	@Override
	public void handle(Packet packet) {
		super.handle(packet);
	}

	@SafeVarargs
	@Override
	public final void a(Packet packet, GenericFutureListener<? extends io.netty.util.concurrent.Future<? super Void>> genericFutureListener, GenericFutureListener<? extends io.netty.util.concurrent.Future<? super Void>>... agenericfuturelistener) {

	}

	@Override
	public SocketAddress getSocketAddress() {
		return new SocketAddress() {
		};
	}

	@Override
	public void close(IChatBaseComponent ichatbasecomponent) {
		this.disconnectReason = ichatbasecomponent;
	}

	@Override
	public boolean c() {
		return false;
	}

	@Override
	public void a(SecretKey secretkey) {

	}

	@Override
	public IChatBaseComponent j() {
		return this.disconnectReason;
	}

	@Override
	public void k() {

	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception {

	}

}
