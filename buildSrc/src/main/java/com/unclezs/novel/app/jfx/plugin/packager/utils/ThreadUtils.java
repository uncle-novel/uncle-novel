package com.unclezs.novel.app.jfx.plugin.packager.utils;

/**
 * Thread utils
 */
public class ThreadUtils {

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Logger.error(e.getMessage());
		}
	}

}
