package org.example;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        TaskExecutorService taskExecutorService = new TaskExecutorService(5);

        TaskGroup taskGroup1 = new TaskGroup(UUID.randomUUID());
        TaskGroup taskGroup2 = new TaskGroup(UUID.randomUUID());
        Future<String> future1 = taskExecutorService.submitTask(new Task<>(UUID.randomUUID(), taskGroup1, TaskType.WRITE, () -> {
            System.out.println("task1 started");
            return "task1 completed";
        }));
        Future<String> future2 = taskExecutorService.submitTask(new Task<>(UUID.randomUUID(), taskGroup1, TaskType.READ, () -> {
            System.out.println("task2 started");
            return "task2 completed";
        }));
        Future<String> future3 = taskExecutorService.submitTask(new Task<>(UUID.randomUUID(), taskGroup2, TaskType.WRITE, () -> {
            System.out.println("task3 started");
            return "task3 completed";
        }));
        Future<String> future4 = taskExecutorService.submitTask(new Task<>(UUID.randomUUID(), taskGroup2, TaskType.READ, () -> {
            System.out.println("task4 started");
            return "task4 completed";
        }));

        System.out.println("task1 output " + future1.get());
        System.out.println("task2 output " + future2.get());
        System.out.println("task3 output " + future3.get());
        System.out.println("task4 output " + future4.get());


        taskExecutorService.shutdown();
        System.out.println("after executor service");
    }

}
