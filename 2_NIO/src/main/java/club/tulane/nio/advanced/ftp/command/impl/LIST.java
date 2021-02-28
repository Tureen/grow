package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpHandler;
import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

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
            final byte[] bytes;
            if (StringUtils.isBlank(content)) {
                bytes = " ".getBytes(CharsetUtil.UTF_8);
            }else{
                bytes = content.getBytes(CharsetUtil.UTF_8);
            }

            final Promise<Void> promise = session.getPortDataClient().writeAndFlushData(channel -> {
                ByteBuf buffer = channel.alloc().buffer(bytes.length).writeBytes(bytes);
                return channel.writeAndFlush(buffer);
            });

            promise.addListener(future -> {
                FtpHandler.sendResponse(new FtpResponse(FtpReply.REPLY_226), session.getCtx()).addListener(future1 -> {
                    session.getPortDataClient().close();
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
