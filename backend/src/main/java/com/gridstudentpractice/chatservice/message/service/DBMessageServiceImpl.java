package com.gridstudentpractice.chatservice.message.service;

import com.gridstudentpractice.chatservice.message.DB_Util;
import com.gridstudentpractice.chatservice.message.model.Message;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
 public class DBMessageServiceImpl implements MessageService {

    final static String insertTableSQL = "INSERT INTO messages (sender, body, time1) VALUES (\'?\', \'?\',\'?\')" ;
    final static String selectTableSQL = "SELECT sender, body, time1 from messages";

    @Override
    public Message sendMessage(Message message) {

        String mSender = message.getMSender();
        String mBody = message.getMBody();
        String mTime = message.getMTimestamp().toString();

        try (PreparedStatement preparedStatement = DB_Util.getConnection().prepareStatement(insertTableSQL)) {

            preparedStatement.setString(1, mSender);
            preparedStatement.setString(2, mBody);
            preparedStatement.setString(3, mTime);

            preparedStatement.executeUpdate();
            System.out.println("SQL injection completed");

            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Message> getMessages() {

        try (Statement statement = DB_Util.getConnection().createStatement()) {
            try (ResultSet rs = statement.executeQuery(selectTableSQL)) {
                List<Message> messages = new ArrayList<>();

                while (rs.next()) {

                    String sender = rs.getString("sender");
                    String body = rs.getString("body");
                    String time1 = rs.getString("time1");

                    Message message = new Message();
                    message.setMSender(sender);
                    message.setMBody(body);
                    message.setMTimestamp(LocalDateTime.parse(time1));

                    messages.add(message);

                    String out = String.format("%s %s : %s", time1, sender, body);
                    System.out.println(out);

                    statement.close();
                }

                return messages;

            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

