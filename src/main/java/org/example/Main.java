package org.example;

import org.example.Mannagers.LotteryManager;
import org.example.Mannagers.LotteryManagerHashMap;


import org.example.RunnableMySQL.RunnableMySQLCashOut;
import org.example.RunnableMySQL.RunnableMySQLGenerate;
import org.example.RunnableRedis.RunnableRedisManagerCashOut;
import org.example.RunnableRedis.RunnableRedisManagerGenerate;

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
            System.out.println("Cash out, (2)");
            System.out.println("Generate new Tickets, Tickets HashMap (3)");
            System.out.println("Cash out, HashMap (4)");
            System.out.println("Generate new Tickets, Runnable Redis (5)");
            System.out.println("Cash out, Runnable Redis (6)");
            System.out.println("Generate new Tickets, Runnable MySQL (7)");
            System.out.println("Cash out, Runnable MySQL (8)");
            System.out.println("Generate new Tickets, RabbitMQ (9)");
            System.out.println("Cash out, RabbitMQ (10)");
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
                case 7:
                    System.out.println("Select amount to generate");
                    amount = Custom.nuskaitytiIntVerteCon();
                    militime = System.currentTimeMillis();
                    executor = Executors.newFixedThreadPool(5);
                    for(int i = 0; i < amount; i++){
                        executor.execute(new RunnableMySQLGenerate());
                    }

                    executor.shutdown();
                    executor.awaitTermination(10, TimeUnit.SECONDS);

                    System.out.println(amount + " tickets were generated");
                    System.out.println("Time in milis: " + (System.currentTimeMillis()  - militime));
                    break;
                case 8:
                    militime = System.currentTimeMillis();

                    RunnableMySQLCashOut runnableMySQLCashOut = new RunnableMySQLCashOut();
                    amount = RunnableMySQLCashOut.getWorkLoadSize();

                    executor = Executors.newFixedThreadPool(5);
                    for(int i = 0; i < amount; i++){
                        executor.execute(runnableMySQLCashOut);
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