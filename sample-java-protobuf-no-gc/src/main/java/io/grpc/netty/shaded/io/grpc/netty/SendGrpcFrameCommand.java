package io.grpc.netty.shaded.io.grpc.netty;

import io.grpc.netty.shaded.io.grpc.netty.WriteQueue.QueuedCommand;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.buffer.ByteBufHolder;
import io.grpc.netty.shaded.io.netty.buffer.DefaultByteBufHolder;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import io.grpc.netty.shaded.io.netty.channel.ChannelPromise;
import io.perfmark.Link;
import io.perfmark.PerfMark;

final class SendGrpcFrameCommand extends DefaultByteBufHolder implements QueuedCommand {
    private final StreamIdHolder stream;
    private final boolean endStream;
    private final Link link;
    private ChannelPromise promise;

    SendGrpcFrameCommand(StreamIdHolder stream, ByteBuf content, boolean endStream) {
        super(content);
        this.stream = stream;
        this.endStream = endStream;
        this.link = PerfMark.linkOut();
    }

    public Link getLink() {
        return this.link;
    }

    StreamIdHolder stream() {
        return this.stream;
    }

    boolean endStream() {
        return this.endStream;
    }

    public ByteBufHolder copy() {
        return new SendGrpcFrameCommand(this.stream, this.content().copy(), this.endStream);
    }

    public ByteBufHolder duplicate() {
        return new SendGrpcFrameCommand(this.stream, this.content().duplicate(), this.endStream);
    }

    public SendGrpcFrameCommand retain() {
        super.retain();
        return this;
    }

    public SendGrpcFrameCommand retain(int increment) {
        super.retain(increment);
        return this;
    }

    public SendGrpcFrameCommand touch() {
        super.touch();
        return this;
    }

    public SendGrpcFrameCommand touch(Object hint) {
        super.touch(hint);
        return this;
    }

    public boolean equals(Object that) {
        if (that != null && that.getClass().equals(SendGrpcFrameCommand.class)) {
            SendGrpcFrameCommand thatCmd = (SendGrpcFrameCommand)that;
            return thatCmd.stream.equals(this.stream) && thatCmd.endStream == this.endStream && thatCmd.content().equals(this.content());
        } else {
            return false;
        }
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(streamId=" + this.stream.id() + ", endStream=" + this.endStream + ", content=" + this.content() + ")";
    }

    public int hashCode() {
        int hash = this.content().hashCode();
        hash = hash * 31 + this.stream.hashCode();
        if (this.endStream) {
            hash = -hash;
        }

        return hash;
    }

    public ChannelPromise promise() {
        return this.promise;
    }

    public void promise(ChannelPromise promise) {
        this.promise = promise;
    }

    public final void run(Channel channel) {
        channel.write(this, this.promise);
    }
}
