package org.example.RunnableMySQL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.DataBaseInfo;
import org.example.LotteryTicket;
import org.example.RedisService;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class RunnableMySQLCashOut implements Runnable{

    private static List<String> concurrent;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static volatile int[] luckyNumbers;
    public static volatile int workLoadSize;

    public RunnableMySQLCashOut() {
        if(concurrent == null){
            luckyNumbers = generateLuckyNumbers(5);
            concurrent = new CopyOnWriteArrayList<>(getTickets());
            workLoadSize = concurrent.size();

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


        String json = getJsonTicketNumbers(ticketNumber);

        int[] numbers = generateObjectFromJSon(json, int[].class);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Your ticket number is: ")
                .append(ticketNumber)
                .append("\n")
                .append(printLuckyNumbers(numbers))
                .append("Lucky ")
                .append("\n")
                .append(printLuckyNumbers(luckyNumbers));

        int matches = calculateWinnings(luckyNumbers, numbers);

        stringBuilder.append(getWinningAmountMessage(matches) + "\n")
                .append(Thread.currentThread().getName());

        deleteTicket(ticketNumber);
        System.out.println(stringBuilder);

    }

    private List<String> getTickets(){
        List<String> ticketList = new ArrayList<>();

        String sql = "SELECT * FROM active_tickets;";
        try {
            Connection connection = DriverManager.getConnection(DataBaseInfo.URL, DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
               ticketList.add(resultSet.getString("ticket_UUID"));
            }

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
        return ticketList;
    }
    private String getJsonTicketNumbers(String ticketNumber){

        String jSonNumbers;
        String sql = "SELECT * FROM active_tickets WHERE ticket_UUID = ?";
        try {
            Connection connection = DriverManager.getConnection(DataBaseInfo.URL, DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,ticketNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()) return "[]";
            jSonNumbers = resultSet.getString("numbers");

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return "[]";
        }
        return jSonNumbers;
    }

    private void deleteTicket(String ticketNumber){

        String sql = "DELETE FROM active_tickets WHERE ticket_UUID = ?";
        try {
            Connection connection = DriverManager.getConnection(DataBaseInfo.URL, DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,ticketNumber);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

        }catch (SQLException e) {
            System.out.println(e.getMessage());
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