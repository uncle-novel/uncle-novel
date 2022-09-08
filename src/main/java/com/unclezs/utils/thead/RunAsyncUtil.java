package com.unclezs.utils.thead;

import cn.hutool.core.thread.ThreadUtil;

/**
 * 延迟异步运行
 *
 * @author uncle
 * @date 2020/4/25 22:12
 */
public class RunAsyncUtil {
    /**
     * 延迟运行 很少用到所以就简单实现了
     *
     * @param runnable /
     * @param delay    毫秒
     */
    public static void run(Runnable runnable, int delay) {
        ThreadUtil.execute(() -> {
            ThreadUtil.sleep(delay);
            runnable.run();
        });
    }
}
