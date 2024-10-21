package org.example.Mannagers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.LotteryTicket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class LotteryManagerHashMap {

    private static final HashMap<String,int[]> alternativeRedisHashMap = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int[] statistics = new int[6];


    public static String generateTickets(int numberOfTickets){

        for(int i = 0; i < numberOfTickets; i++){

            LotteryTicket lotteryTicket = new LotteryTicket();

            alternativeRedisHashMap.put(lotteryTicket.getTicketNumber().toString(), lotteryTicket.getNumbers());

        }
        return numberOfTickets + " tickets were generated";
    }

    public static void cashOut(){
        Set<String> ticketKeys;
        int[] luckyNumbers =  generateLuckyNumbers(5);

            ticketKeys = alternativeRedisHashMap.keySet();
            Iterator<String> iterator = ticketKeys.iterator();

            while (iterator.hasNext()){

                String ticketNumber = iterator.next();

                int[] numbers = alternativeRedisHashMap.get(ticketNumber);


                System.out.println("Your ticket number is: " + ticketNumber);
                printLuckyNumbers(numbers);

                System.out.print("Lucky ");
                printLuckyNumbers(luckyNumbers);

                int matches = calculateWinnings(luckyNumbers,numbers);
                System.out.println(getWinningAmountMessage(matches));


                System.out.println();

            }
            alternativeRedisHashMap.clear();

            System.out.println();
            System.out.println("Total statistics");
            printLuckyNumbers(statistics);
            System.out.println();

    }


    private static <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

    private static <T> T generateObjectFromJSon(String json, Class<T> clazz){
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

    private static void printLuckyNumbers(int [] numbers){
        System.out.println("Numbers are:");
        for(int i:numbers) System.out.print(i + " ");
        System.out.println();
    }

    private static int calculateWinnings(int[] luckyNumbers, int[] numbers){
        int count = 0;
        for(int i = 0; i < luckyNumbers.length; i++){
            for(int j = 0; j < numbers.length; j++){
                if(luckyNumbers[i]==numbers[j]){
                    count++;
                    numbers[j] = -1;
                }
            }
        }
        addToStatistics(count);
        return count;
    }

    private static String getWinningAmountMessage(int amount){
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
    private static void addToStatistics(int amount){
        statistics[amount] = statistics[amount] + 1;
    }



}
