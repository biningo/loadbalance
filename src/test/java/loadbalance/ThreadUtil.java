package loadbalance;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    public static void waitTasks(ThreadPoolExecutor executor) {
        while (executor.getActiveCount() > 0 || !executor.getQueue().isEmpty()) {
            ThreadUtil.sleepIgnoreInterceptor(10, TimeUnit.MILLISECONDS);
        }
    }

    public static void sleepIgnoreInterceptor(int time, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(time));
        } catch (InterruptedException ignored) {
        }
    }
}
