package cn.gjing;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author Gjing
 * 线程池工具类
 **/
public class ExecutorUtil {

    /**
     * 根据CPU的数量动态的配置核心线程数和最大线程数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 线程池核心线程数 = CPU + 1
     */
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /**
     * 线程池最大线程数 = CPU核心数 * 2 + 1
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 非核心线程数闲置时超时时间默认1s
     */
    private static final int KEEP_ALIVE = 1;

    /**
     * 队列
     */
    private static final int QUEUE = CPU_COUNT * 3;
    /**
     * 线程池对象
     */
    private static ThreadPoolExecutor threadPool = null;
    /**
     * 线程工常
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("gjing-%d").build();

    /**
     * 获取线程池
     *
     * @return 线程池对象
     */
    private static ThreadPoolExecutor getThreadPool() {
        if (threadPool != null) {
            return threadPool;
        } else {
            synchronized (ExecutorUtil.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new ArrayBlockingQueue<>(QUEUE), THREAD_FACTORY,
                            new ThreadPoolExecutor.AbortPolicy());
                }
                return threadPool;
            }
        }
    }

    /**
     * 无返回值
     *
     * @param runnable r
     */
    public static void execute(Runnable runnable) {
        getThreadPool().execute(runnable);
    }

    /**
     * 返回值
     *
     * @param callable c
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }

    /**
     * 将任务移除等待队列
     *
     * @param runnable 任务
     */
    public static void cancel(Runnable runnable) {
        if (runnable != null) {
            getThreadPool().getQueue().remove(runnable);
        }
    }
}

