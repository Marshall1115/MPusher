package com.example.administrator.mpush.utils;


import com.example.administrator.mpush.AppContext;

/**
 * Created by Administrator on 2016/4/21.
 */
public class HandlerUtils {
	/**
	 * 执行一个延迟的任务
	 * @param task
	 */
	public static void postTaskDelay(Runnable task, long delayTime) {
		AppContext.getHandler().postDelayed(task, delayTime);
	}

	public static void removeCallbacks(Runnable task) {
		AppContext.getHandler().removeCallbacks(task);
	}

	/**
	 * 安全的执行一个任务
	 */
	public static void postTaskSafely(Runnable task) {
		if (AppContext.isMainThread()) {
			task.run();
		} else {
			AppContext.getHandler().post(task);
		}
	}

}
