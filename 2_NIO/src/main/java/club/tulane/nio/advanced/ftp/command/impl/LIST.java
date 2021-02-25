package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class LIST implements Command {

    private static final LISTFileFormatter LIST_FILE_FORMATER = new LISTFileFormatter();

    private final DirectoryLister directoryLister = new DirectoryLister();

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        final FtpResponse response = new FtpResponse(FtpReply.REPLY_150);

        response.setChannelPromise(
                session.getCtx().newPromise().addListener(future -> {
                    doSendFileList(session, request);
                })
        );
        return response;
    }

    private void doSendFileList(FtpSession session, FtpRequest request) {
        try {
            ListArgument parsedArg = ListArgument.parse(request.getArgument());
            String content = directoryLister.listFiles(parsedArg, session.getFileSystemView(), LIST_FILE_FORMATER);
            content += "\r\n";

            byte[] bytes = content.getBytes(CharsetUtil.UTF_8);
            if (bytes.length == 0) {
                bytes = " ".getBytes(CharsetUtil.UTF_8);
            }
            final Channel channel = session.getCtx().channel();
            ByteBuf buffer = channel.alloc().buffer(bytes.length).writeBytes(bytes);
            channel.writeAndFlush(("200 " + content).getBytes(CharsetUtil.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
