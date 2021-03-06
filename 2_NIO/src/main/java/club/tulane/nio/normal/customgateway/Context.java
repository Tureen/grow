package club.tulane.nio.normal.customgateway;

import club.tulane.nio.normal.customgateway.filter.FilterPipeline;

public class Context {

    private static FilterPipeline filterPipeline;

    private Context() {
    }

    static {
        filterPipeline = new FilterPipeline();
    }

    public static FilterPipeline getPipeline(){
        return filterPipeline;
    }
}
