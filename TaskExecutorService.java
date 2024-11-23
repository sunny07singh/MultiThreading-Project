package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TaskExecutorService implements TaskExecutor {
    private final Queue<Runnable> tasksQueue;
    private boolean shutdownTriggered = false;
    List<WorkerThread> workerThreadList = new ArrayList<>();

    private final Object lock = new Object();

    public TaskExecutorService(int maxThreads) {
        this.tasksQueue = new LinkedList<>();
        for(int i=0 ;i<maxThreads; i++){
            WorkerThread workerThread = new WorkerThread(tasksQueue, this);
            workerThreadList.add(workerThread);
        }
        for(WorkerThread workerThread: workerThreadList){
            workerThread.start();
        }
    }

    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(() ->
                task.taskAction().call()
        );
        Runnable runnableTask = futureTask::run;
        synchronized (lock) {
            tasksQueue.add(runnableTask);
            lock.notifyAll();
        }
        return futureTask;
    }

    public void shutdown() {
        shutdownTriggered = true;
    }

    public boolean isShutdownTriggered(){
        return shutdownTriggered;
    }

    private class WorkerThread extends Thread{
        private final Queue<Runnable> tasksQueue;
        private final TaskExecutorService  taskExecutorService;
        public WorkerThread(Queue<Runnable> tasksQueue, TaskExecutorService  taskExecutorService){
            this.tasksQueue=tasksQueue;
            this.taskExecutorService=taskExecutorService;
        }

        public void run(){
            while(!taskExecutorService.isShutdownTriggered()){
                Runnable task;
                synchronized (lock) {
                    while (tasksQueue.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            System.out.println("Thread pool waiting for tasks");
                        }
                    }
                    task = tasksQueue.poll();
                }
                task.run();
            }
        }
    }

}
