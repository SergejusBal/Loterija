package org.example;

import org.example.Mannagers.LotteryManager;
import org.example.Mannagers.LotteryManagerHashMap;


import org.example.RabbitMQ.RabbitMQService;
import org.example.RunnableMySQL.RunnableMySQLCashOut;
import org.example.RunnableMySQL.RunnableMySQLGenerate;
import org.example.RunnableRedis.RunnableRedisManagerCashOut;
import org.example.RunnableRedis.RunnableRedisManagerGenerate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    static int amount;
    static long militime;
    static long[] time = new long[10];
    static RedisService  redisService = new RedisService("localhost", 6379);
    static LotteryManager lotteryManager = new LotteryManager(redisService);
    static RabbitMQService rabbitMQService = new RabbitMQService("LotteryTickets");
    public static void main(String[] args) throws Exception {

        ExecutorService executor;

//        RedisService redisService = new RedisService("localhost", 6379);
//        LotteryManager lotteryManager = new LotteryManager(redisService);
//        RabbitMQService rabbitMQService = new RabbitMQService("LotteryTickets");


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
            System.out.println("Speed statistics (11)");
            System.out.println("Quit: (0)");


            int operation = Custom.nuskaitytiIntVerteCon();
            switch (operation){
                case 1:
                    System.out.println("Select amount to generate");
                    amount = Custom.nuskaitytiIntVerteCon();
                    militime = System.currentTimeMillis();
                    System.out.println(lotteryManager.generateTickets(amount));
                    time[0] = (System.currentTimeMillis()  - militime);
                    break;
                case 2:
                    militime = System.currentTimeMillis();
                    lotteryManager.cashOut();
                    time[1] = (System.currentTimeMillis()  - militime);
                    break;
                case 3:
                    System.out.println("Select amount to generate");
                    amount = Custom.nuskaitytiIntVerteCon();
                    militime = System.currentTimeMillis();
                    System.out.println(LotteryManagerHashMap.generateTickets(amount));
                    time[2] = (System.currentTimeMillis()  - militime);
                    break;
                case 4:
                    militime = System.currentTimeMillis();
                    LotteryManagerHashMap.cashOut();
                    time[3] = (System.currentTimeMillis()  - militime);
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
                    executor.awaitTermination(100, TimeUnit.SECONDS);
                    executor.shutdownNow();

                    System.out.println(amount + " tickets were generated");
                    time[4] = (System.currentTimeMillis()  - militime);
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
                    executor.awaitTermination(100, TimeUnit.SECONDS);
                    executor.shutdownNow();

                    time[5] = (System.currentTimeMillis()  - militime);
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
                    executor.awaitTermination(1000, TimeUnit.SECONDS);
                    executor.shutdownNow();

                    System.out.println(amount + " tickets were generated");
                    time[6] = (System.currentTimeMillis()  - militime);
                    break;
                case 8:
                    militime = System.currentTimeMillis();

                    RunnableMySQLCashOut runnableMySQLCashOut = new RunnableMySQLCashOut();
                    amount = runnableMySQLCashOut.getWorkLoadSize();

                    executor = Executors.newFixedThreadPool(5);
                    for(int i = 0; i < amount; i++){
                        executor.execute(runnableMySQLCashOut);
                    }
                    executor.shutdown();
                    executor.awaitTermination(1000, TimeUnit.SECONDS);
                    executor.shutdownNow();

                    time[7] = (System.currentTimeMillis()  - militime);
                    break;
                case 9:
                    System.out.println("Select amount to generate");
                    amount = Custom.nuskaitytiIntVerteCon();

                    militime = System.currentTimeMillis();
                    rabbitMQService.createAndSendTickets(amount);

                    System.out.println(amount + " tickets were generated");
                    time[8] = (System.currentTimeMillis()  - militime);
                    break;
                case 10:
                    militime = System.currentTimeMillis();

                    rabbitMQService.cashOutTickets();

                    time[9] = (System.currentTimeMillis()  - militime);
                    break;

                case 11:
                    System.out.println("Redis generate: " + time[0] + "ms");
                    System.out.println("Redis pop: " + time[1] + "ms");
                    System.out.println("HashMap generate: " + time[2] + "ms");
                    System.out.println("HashMap pop: " + time[3] + "ms");
                    System.out.println("Redis Runnable generate: " + time[4] + "ms");
                    System.out.println("Redis Runnable pop: " + time[5] + "ms");
                    System.out.println("MySQL Runnable generate: " + time[6] + "ms");
                    System.out.println("MySQL Runnable pop: " + time[7] + "ms");
                    System.out.println("RabbitMQ generate: " + time[8] + "ms");
                    System.out.println("RabbotMQ pop: " + time[9] + "ms");
                    break;
                case 12:
                    generateAll();
                    break;
                case 13:
                    cashAll();
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

//        Redis generate: 39780ms
//        Redis pop: 130757ms
//        HashMap generate: 132ms
//        HashMap pop: 2944ms
//        Redis Runnable generate: 11106ms
//        Redis Runnable pop: 31537ms
//        MySQL Runnable generate: 244094ms
//        MySQL Runnable pop: 262711ms
//        RabbitMQ generate: 2418ms
//        RabbotMQ pop: 1626ms

    }

    public static void generateAll(){
        Runnable redisG = new Runnable() {
            @Override
            public void run() {
                LotteryManager lotteryManager = new LotteryManager(redisService);
                System.out.println("Select amount to generate");
                amount = Custom.nuskaitytiIntVerteCon();
                militime = System.currentTimeMillis();
                System.out.println(lotteryManager.generateTickets(amount));
                time[0] = (System.currentTimeMillis()  - militime);

            }
        };
        Thread thread01 = new Thread(redisG);
        thread01.start();

        Runnable hashMap = new Runnable() {
            @Override
            public void run() {
                System.out.println("Select amount to generate");
                amount = Custom.nuskaitytiIntVerteCon();
                militime = System.currentTimeMillis();
                System.out.println(LotteryManagerHashMap.generateTickets(amount));
                time[2] = (System.currentTimeMillis()  - militime);
            }
        };

        Thread thread02 = new Thread(hashMap);
        thread02.start();

        Runnable redisRunnable = new Runnable() {
            @Override
            public void run() {
                ExecutorService executor;
                System.out.println("Select amount to generate");
                amount = Custom.nuskaitytiIntVerteCon();
                militime = System.currentTimeMillis();

                executor = Executors.newFixedThreadPool(5);
                for(int i = 0; i < amount; i++){
                    executor.execute(new RunnableRedisManagerGenerate(redisService));
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(100, TimeUnit.SECONDS);
                    executor.shutdownNow();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(amount + " tickets were generated");
                time[4] = (System.currentTimeMillis()  - militime);
            }
        };

        Thread thread03 = new Thread(redisRunnable);
        thread03.start();


    }
    public static void cashAll(){
        Runnable redisC = new Runnable() {
            @Override
            public void run() {
                militime = System.currentTimeMillis();
                lotteryManager.cashOut();
                time[1] = (System.currentTimeMillis()  - militime);
            }
        };
        Thread thread01 = new Thread(redisC);
        thread01.start();

        Runnable hashMapC = new Runnable() {
            @Override
            public void run() {
                militime = System.currentTimeMillis();
                LotteryManagerHashMap.cashOut();
                time[3] = (System.currentTimeMillis()  - militime);
            }
        };
        Thread thread02 = new Thread(hashMapC);
        thread02.start();

        Runnable redisRunnable = new Runnable() {
            @Override
            public void run() {
                ExecutorService executor;
                militime = System.currentTimeMillis();

                RunnableRedisManagerCashOut runnableRedisManagerCashOut = new RunnableRedisManagerCashOut(redisService);
                amount = RunnableRedisManagerCashOut.getWorkLoadSize();

                executor = Executors.newFixedThreadPool(5);
                for(int i = 0; i < amount; i++){
                    executor.execute(runnableRedisManagerCashOut);
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(100, TimeUnit.SECONDS);
                    executor.shutdownNow();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                time[5] = (System.currentTimeMillis()  - militime);
            }
        };

        Thread thread03 = new Thread(redisRunnable);
        thread03.start();




    }



}