package club.tulane.nio.advanced.common;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ImproperOptionException extends SystemException {

    public ImproperOptionException(HttpResponseStatus status) {
        super(status);
    }
}
