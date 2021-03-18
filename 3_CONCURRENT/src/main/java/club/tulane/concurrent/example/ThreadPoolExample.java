package club.tulane.concurrent.example;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolExample {

    public ThreadPoolExecutor initThreadPoolExecutor(){
        int coreSize = Runtime.getRuntime().availableProcessors();
        int maxSize = coreSize * 2;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>(500);
        CustomThreadFactory threadFactory = new CustomThreadFactory();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(coreSize, maxSize,1, TimeUnit.MINUTES, workQueue, threadFactory);
        return executor;
    }

    class CustomThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "custom-" + threadNumber.getAndIncrement());
            return t;
        }
    }
}
