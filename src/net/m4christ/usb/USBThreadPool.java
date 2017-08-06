package net.m4christ.usb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class USBThreadPool {
	private int corePoolSize = 26;
	private int maxPoolSize = 26;
	private long keepAliveTime = 5000L;
	private ExecutorService threadPoolExecutor = new ThreadPoolExecutor(
			this.corePoolSize, this.maxPoolSize, this.keepAliveTime,
			TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	private static USBThreadPool tp = new USBThreadPool();

	public USBThreadPool() {
	}

	public static USBThreadPool getInstance() {
		return tp;
	}

	public void run(Runnable r) {
		tp.threadPoolExecutor.submit(r);
	}
}
