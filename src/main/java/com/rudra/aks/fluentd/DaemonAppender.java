package com.rudra.aks.fluentd;

import java.util.concurrent.BlockingQueue;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import java.util.concurrent.LinkedBlockingQueue;

import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicBoolean;



import org.slf4j.Logger;

import org.slf4j.LoggerFactory;


public abstract class DaemonAppender<E> implements Runnable {

    private static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private static final Logger LOG = LoggerFactory.getLogger(DaemonAppender.class);

    private AtomicBoolean start = new AtomicBoolean(false);

    private final BlockingQueue<E> queue;

    private boolean stopThread = false;
    
    DaemonAppender(int maxQueueSize) {
        this.queue = new LinkedBlockingQueue<E>(maxQueueSize);
    }

    protected void execute() {
        if (THREAD_POOL.isShutdown()) {
            THREAD_POOL = Executors.newCachedThreadPool();
        }
        THREAD_POOL.execute(this);
    }

    void log(E eventObject) {
        if (!queue.offer(eventObject)) {
            LOG.warn("Message queue is full. Ignored the message:" + System.lineSeparator() + eventObject.toString());
        } else if (start.compareAndSet(false, true)) {
            execute();
        }
    }

    //@Override
    public void run() {
        try {
            for (;;) {
            	if(null != queue)
            		append(queue.take());
            	else break;
            }
        } catch (InterruptedException e) {
            // ignore the error and rerun.
        	if(!stopThread)
        		run();
        } catch (Exception e) {
            close();
        }
    }

    abstract protected void append(E rawData);

    protected void close() {
        synchronized (THREAD_POOL) {
        	stopThread = true;
        	Thread.currentThread().interrupt();
            if (!THREAD_POOL.isShutdown()) {
                shutdownAndAwaitTermination(THREAD_POOL);
            }
        }
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdownNow();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}