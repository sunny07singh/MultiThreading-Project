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

    public TaskExecutorService(int maxThreads) {
        this.tasksQueue = new LinkedList<>();
        for(int i=0 ;i<maxThreads; i++){
            WorkerThread workerThread = new WorkerThread(tasksQueue, this);
            workerThreadList.add(workerThread);
        }
        for(WorkerThread workerThread: workerThreadList){
            workerThread.start();
        }
       // executeQueue();
    }

    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        FutureTask futureTask = new FutureTask<>(() ->
                task.taskAction().call()
        );
        Runnable runnableTask = ()-> futureTask.run();
        synchronized (tasksQueue) {
            tasksQueue.add(runnableTask);
            tasksQueue.notifyAll();
        }
        return futureTask;
    }

    public void shutdown() {
        shutdownTriggered = true;
    }

    public boolean isShutdownTriggered(){
        return shutdownTriggered;
    }

}
