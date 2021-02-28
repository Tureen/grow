package club.tulane.nio.advanced.ftp.command.impl;

import club.tulane.nio.advanced.ftp.FtpPortDataClient;
import club.tulane.nio.advanced.ftp.FtpReply;
import club.tulane.nio.advanced.ftp.command.Command;
import club.tulane.nio.advanced.ftp.model.FtpRequest;
import club.tulane.nio.advanced.ftp.model.FtpResponse;
import club.tulane.nio.advanced.ftp.model.FtpSession;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * ftp命令: ls
 */
public class PORT implements Command {

    @Override
    public FtpResponse execute(FtpRequest request, FtpSession session) {
        InetSocketAddress address = null;
        try {
            address = decode(request.getArgument());
            session.setPortDataClient(new FtpPortDataClient(session, address));
        } catch (Exception e) {
            return new FtpResponse(FtpReply.REPLY_501);
        }
        return new FtpResponse(FtpReply.REPLY_200);
    }

    public static InetSocketAddress decode(String str)
            throws UnknownHostException {
        StringTokenizer st = new StringTokenizer(str, ",");
        if (st.countTokens() != 6) {
            throw new IllegalStateException("Illegal amount of tokens");
        }

        StringBuilder sb = new StringBuilder();
        try {
            sb.append(convertAndValidateNumber(st.nextToken()));
            sb.append('.');
            sb.append(convertAndValidateNumber(st.nextToken()));
            sb.append('.');
            sb.append(convertAndValidateNumber(st.nextToken()));
            sb.append('.');
            sb.append(convertAndValidateNumber(st.nextToken()));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e.getMessage());
        }

        InetAddress dataAddr = InetAddress.getByName(sb.toString());

        // get data server port
        int dataPort = 0;
        try {
            int hi = convertAndValidateNumber(st.nextToken());
            int lo = convertAndValidateNumber(st.nextToken());
            dataPort = (hi << 8) | lo;
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Invalid data port: " + str);
        }

        return new InetSocketAddress(dataAddr, dataPort);
    }

    private static int convertAndValidateNumber(String s) {
        int i = Integer.parseInt(s);
        if (i < 0) {
            throw new IllegalArgumentException("Token can not be less than 0");
        } else if (i > 255) {
            throw new IllegalArgumentException(
                    "Token can not be larger than 255");
        }
        return i;
    }
}
