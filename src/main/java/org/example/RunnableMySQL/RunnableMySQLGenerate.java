package org.example.RunnableMySQL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.DataBaseInfo;
import org.example.LotteryTicket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RunnableMySQLGenerate implements Runnable{

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void run() {

        LotteryTicket lotteryTicket = new LotteryTicket();
        writeTicket(lotteryTicket.getTicketNumber().toString(),generateJson(lotteryTicket.getNumbers()));
        System.out.println(Thread.currentThread().getName());
    }

    private void writeTicket(String UUID, String jsonNumbers){

        String sql = "INSERT INTO active_tickets (ticket_UUID,numbers) VALUES (?,?)";
        try {
            Connection connection = DriverManager.getConnection(DataBaseInfo.URL, DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, UUID);
            preparedStatement.setString(2, jsonNumbers);

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


}
