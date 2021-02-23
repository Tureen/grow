package club.tulane.nio.advanced.common;

import io.netty.handler.codec.http.HttpResponseStatus;

public class NormalOptionException extends SystemException {

    public NormalOptionException(HttpResponseStatus status) {
        super(status);
    }
}
