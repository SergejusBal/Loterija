package org.example.RabbitMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.example.LotteryTicket;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RabbitMQService {

    private String queueName;
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static volatile int[] luckyNumbers;
    private AtomicInteger messageCount;

    public RabbitMQService(String queueName) {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.queueName = queueName;
        luckyNumbers = generateLuckyNumbers(5);
        messageCount = new AtomicInteger();
    }

    public  void  createAndSendTickets(int amount) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);

            for(int i = 0; i < amount; i++) {
                String jsonMessage = generateJson(new LotteryTicket());
                channel.basicPublish("", queueName, null, jsonMessage.getBytes());
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void cashOutTickets () throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);

            AMQP.Queue.DeclareOk result = channel.queueDeclarePassive(queueName);
            messageCount.set(result.getMessageCount());

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String jsonMessage = new String(delivery.getBody(), "UTF-8");

                try {
                    LotteryTicket lotteryTicket = objectMapper.readValue(jsonMessage, LotteryTicket.class);
                    int[] numbers = lotteryTicket.getNumbers();

                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append("Your ticket number is: ")
                            .append(lotteryTicket.getTicketNumber())
                            .append("\n")
                            .append(printLuckyNumbers(numbers))
                            .append("Lucky ")
                            .append(printLuckyNumbers(luckyNumbers));

                    int matches = calculateWinnings(luckyNumbers, numbers);

                    stringBuilder.append(getWinningAmountMessage(matches) + "\n")
                            .append(Thread.currentThread().getName());

                    System.out.println(stringBuilder);
                    messageCount.decrementAndGet();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            while (messageCount.get() != 0) {
                Thread.sleep(10);
            }
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

    private int[] generateLuckyNumbers(int size){
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


}
