package club.tulane.nio.normal.customgateway.filter;

import io.netty.handler.codec.http.FullHttpRequest;

public interface Filter {

    void filter(FullHttpRequest fullRequest);
}
