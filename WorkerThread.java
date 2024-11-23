package org.example;

import java.util.Queue;

public class WorkerThread extends Thread{
    private final Queue<Runnable> tasksQueue;
    private final TaskExecutorService  taskExecutorService;
    public WorkerThread(Queue<Runnable> tasksQueue, TaskExecutorService  taskExecutorService){
           this.tasksQueue=tasksQueue;
           this.taskExecutorService=taskExecutorService;
    }

    public void run(){
        while(!taskExecutorService.isShutdownTriggered()){
            synchronized (tasksQueue) {
                while (tasksQueue.isEmpty()) {
                    try {
                        tasksQueue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Thread pool waiting for tasks");
                    }
                }
                Runnable task = tasksQueue.poll();
                task.run();
            }
        }
    }
}
