package org.example.RunnableRedis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.RedisService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;



public class RunnableRedisManagerCashOut implements Runnable{

    private static List<String> concurrent;
    private RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static volatile int[] luckyNumbers;
    public static volatile int workLoadSize;

    public RunnableRedisManagerCashOut(RedisService redisService) {
        this.redisService = redisService;
        if(concurrent == null){
            luckyNumbers = generateLuckyNumbers(5);
            concurrent = new CopyOnWriteArrayList<>();

            try {
                Set<String> keys = redisService.getKeys();
                workLoadSize = keys.size();
                concurrent.addAll(keys);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public synchronized static String getTicketNumber(){
        String key = concurrent.getFirst();
        concurrent.remove(key);
        return key;
    }


    @Override
    public void run() {
        String ticketNumber = getTicketNumber();

        try {
            String json = (String) redisService.get(ticketNumber);


            int[] numbers = generateObjectFromJSon(json, int[].class);

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Your ticket number is: ")
                            .append(ticketNumber)
                            .append("\n")
                            .append(printLuckyNumbers(numbers))
                            .append("Lucky ")
                            .append("\n")
                            .append(printLuckyNumbers(luckyNumbers));

            int matches = calculateWinnings(luckyNumbers,numbers);

            stringBuilder.append(getWinningAmountMessage(matches) + "\n")
                         .append(Thread.currentThread().getName());

            redisService.delete(ticketNumber);
            System.out.println(stringBuilder);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }


    private <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

    private  <T> T generateObjectFromJSon(String json, Class<T> clazz){
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static int[] generateLuckyNumbers(int size){
        Random random = new Random();
        int [] luckyNumbers = new int[size];
        for(int i = 0; i < size; i++ ) luckyNumbers[i] = random.nextInt(1,36);
        return luckyNumbers;
    }

    private String printLuckyNumbers(int [] numbers){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Numbers are: ");
        for(int i:numbers) stringBuilder.append(i + " ");
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    private int calculateWinnings(int[] luckyNumbers, int[] numbers){
        int count = 0;
        for(int i = 0; i < luckyNumbers.length; i++){
            for(int j = 0; j < numbers.length; j++){
                if(luckyNumbers[i]==numbers[j]){
                    count++;
                    numbers[j] = -1;
                }
            }
        }

        return count;
    }

    private String getWinningAmountMessage(int amount){
        switch (amount){
            case 1:
                return "You won 0,50 EUR!";
            case 2:
                return "You won 3 EUR!";
            case 3:
                return "You won 15 EUR!";
            case 4:
                return "You won 500 EUR!";
            case 5:
                return "You won 5000 EUR!";
            default:
                return "Better luck next time!";
        }
    }

    public static int getWorkLoadSize() {
        return workLoadSize;
    }
}
