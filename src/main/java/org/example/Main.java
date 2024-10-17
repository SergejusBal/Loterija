package org.example;

import org.example.Mannagers.LotteryManager;
import org.example.Mannagers.LotteryManagerHashMap;


import org.example.Mannagers.RunnableRedisManagerCashOut;
import org.example.Mannagers.RunnableRedisManagerGenerate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor;

        RedisService redisService = new RedisService("localhost", 6379);
        LotteryManager lotteryManager = new LotteryManager(redisService);


        System.out.println("*********************************************************");
        System.out.println("Lottery Game");
        System.out.println("*********************************************************");

        boolean running = true;
        while(running) {
            System.out.println("Generate new Tickets (1)");
            System.out.println("Cash out (2)");
            System.out.println("Generate new Tickets HashMap (3)");
            System.out.println("Cash out HashMap (4)");
            System.out.println("Generate new Runnable (5)");
            System.out.println("Cash out Runnable (6)");
            System.out.println("Quit: (0)");
            int amount;
            long militime;
            int operation = Custom.nuskaitytiIntVerteCon();
            switch (operation){
                case 1:
                    System.out.println("Select amount to generate");
                    amount = Custom.nuskaitytiIntVerteCon();
                    militime = System.currentTimeMillis();
                    System.out.println(lotteryManager.generateTickets(amount));
                    System.out.println("Time in milis: " + (System.currentTimeMillis()  - militime));
                    break;
                case 2:
                    militime = System.currentTimeMillis();
                    lotteryManager.cashOut();
                    System.out.println("Time in milis: " + (System.currentTimeMillis()  - militime));
                    break;
                case 3:
                    System.out.println("Select amount to generate");
                    amount = Custom.nuskaitytiIntVerteCon();
                    militime = System.currentTimeMillis();
                    System.out.println(LotteryManagerHashMap.generateTickets(amount));
                    System.out.println("Time in milis: " + (System.currentTimeMillis()  - militime));
                    break;
                case 4:
                    militime = System.currentTimeMillis();
                    LotteryManagerHashMap.cashOut();
                    System.out.println("Time in milis: " + (System.currentTimeMillis()  - militime));
                    break;
                case 5:
                    System.out.println("Select amount to generate");
                    amount = Custom.nuskaitytiIntVerteCon();
                    militime = System.currentTimeMillis();

                    executor = Executors.newFixedThreadPool(5);
                    for(int i = 0; i < amount; i++){
                      executor.execute(new RunnableRedisManagerGenerate(redisService));
                    }
                    executor.shutdown();
                    executor.awaitTermination(10, TimeUnit.SECONDS);

                    System.out.println(amount + " tickets were generated");
                    System.out.println("Time in milis: " + (System.currentTimeMillis()  - militime));
                    break;
                case 6:
                    militime = System.currentTimeMillis();

                    RunnableRedisManagerCashOut runnableRedisManagerCashOut = new RunnableRedisManagerCashOut(redisService);
                    amount = RunnableRedisManagerCashOut.getWorkLoadSize();

                    executor = Executors.newFixedThreadPool(5);
                    for(int i = 0; i < amount; i++){
                        executor.execute(runnableRedisManagerCashOut);
                    }
                    executor.shutdown();
                    executor.awaitTermination(10, TimeUnit.SECONDS);

                    System.out.println("Time in milis: " + (System.currentTimeMillis()  - militime));
                    break;
                case 0:
                    running = false;
                    System.out.println("Program is closing...");
                    break;
                default:
                    System.out.println("There is no operation with selected number");
                    break;
            }
            System.out.println("*********************************************************");
        }






    }
}