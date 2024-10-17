package org.example;

import java.util.Random;
import java.util.UUID;

public class LotteryTicket {

    private UUID ticketNumber;
    private int[] numbers;

    public LotteryTicket() {
        ticketNumber = generateUID();
        numbers = generateLuckyNumbers(5);

    }

    private UUID generateUID(){
        return UUID.randomUUID();
    }

    private int[] generateLuckyNumbers(int size){
        Random random = new Random();
        int [] luckyNumbers = new int[size];
        for(int i = 0; i < size; i++ ) luckyNumbers[i] = random.nextInt(1,36);
        return luckyNumbers;
    }

    public UUID getTicketNumber() {
        return ticketNumber;
    }

    public int[] getNumbers() {
        return numbers;
    }
}


