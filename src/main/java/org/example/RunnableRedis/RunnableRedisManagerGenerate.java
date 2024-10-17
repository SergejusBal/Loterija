package org.example.RunnableRedis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.LotteryTicket;
import org.example.RedisService;

import java.io.IOException;

public class RunnableRedisManagerGenerate implements Runnable{

    private RedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RunnableRedisManagerGenerate(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void run() {
        LotteryTicket lotteryTicket = new LotteryTicket();

        try {
            redisService.put(lotteryTicket.getTicketNumber().toString(), generateJson(lotteryTicket.getNumbers()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName());

    }

    private <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

}
