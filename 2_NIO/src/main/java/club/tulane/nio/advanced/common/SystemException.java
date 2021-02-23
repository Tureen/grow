package club.tulane.nio.advanced.common;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemException extends RuntimeException{

    private HttpResponseStatus status;

    public SystemException(HttpResponseStatus status) {
        this.status = status;
    }
}
