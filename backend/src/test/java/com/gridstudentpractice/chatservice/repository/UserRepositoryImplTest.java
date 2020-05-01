package com.gridstudentpractice.chatservice.repository;

import com.gridstudentpractice.chatservice.model.UserDto;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test","orm"})
public class UserRepositoryImplTest {

    @Autowired
    private Connection connection;

    @Autowired
    private UserRepository userRepository;

    private static final String clearUserTableQuery = "DELETE FROM users; " +
                                                        "DELETE FROM chatrooms; " +
                                                        "ALTER TABLE users ALTER COLUMN id RESTART WITH 1; " +
                                                        "ALTER TABLE chatrooms ALTER COLUMN id RESTART WITH 1;";
    private static final String insertUsersQuery = "INSERT INTO users VALUES (1, 'foo1', 'pass1'), " +
                                                                            "(2, 'foo2', 'pass2'), " +
                                                                            "(3, 'foo3', 'pass3');";
    private static final String selectUsersQuery = "SELECT * FROM users;";

    @After
    public void after() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(clearUserTableQuery);
        statement.close();
    }

    private UserDto findUserById (int id, List<UserDto> userDtos) {
        for (UserDto userDto : userDtos) {
            if (userDto.getId() == id) {
                return userDto;
            }
        }
        return null;
    }

    @Test
    public void getUserByLogin() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(insertUsersQuery);

        String userLogin = "foo3";
        UserDto repoUserDto = userRepository.getUserByLogin(userLogin);

        ResultSet rs = statement.executeQuery(selectUsersQuery);
        List<UserDto> userDtos = new ArrayList<>();
        while (rs.next()) {
            UserDto userDto = UserDto.builder()
                    .id(rs.getInt("id"))
                    .login(rs.getString("login"))
                    .password(rs.getString("password"))
                    .build();
            userDtos.add(userDto);
        }
        statement.close();

        for (UserDto userDto : userDtos) {
            if (userDto.getLogin().equals(userLogin)) {
                assertEquals(userDto.getId(), repoUserDto.getId());
                assertEquals(userDto.getLogin(), repoUserDto.getLogin());
                assertEquals(userDto.getPassword(), repoUserDto.getPassword());
            }
        }
    }

    @Test
    public void CreateUser() throws SQLException {
        UserDto foo1 = UserDto.builder()
                .id(1)
                .login("foo1")
                .password("pass1")
                .build();

        userRepository.createUser(foo1);

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(selectUsersQuery);
        if (rs.next()) {
            assertEquals(foo1.getId(), rs.getInt("id"));
            assertEquals(foo1.getLogin(), rs.getString("login"));
            assertEquals(foo1.getPassword(), rs.getString("password"));
        }
        statement.close();
    }

    @Test
    public void updateUser() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(insertUsersQuery);

        UserDto foo1 = UserDto.builder()
                .id(1)
                .login("foo01")
                .password("pass01")
                .build();
        userRepository.updateUser(foo1);

        ResultSet rs = statement.executeQuery(selectUsersQuery);
        List<UserDto> userDtos = new ArrayList<>();
        while (rs.next()) {
            UserDto userDto = UserDto.builder()
                    .id(rs.getInt("id"))
                    .login(rs.getString("login"))
                    .password(rs.getString("password"))
                    .build();
            userDtos.add(userDto);
        }
        statement.close();

        assertEquals(foo1.getId(), findUserById(foo1.getId(), userDtos).getId());
        assertEquals(foo1.getLogin(), findUserById(foo1.getId(), userDtos).getLogin());
        assertEquals(foo1.getPassword(), findUserById(foo1.getId(), userDtos).getPassword());
    }

    @Test
    public void deleteUserById() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(insertUsersQuery);

        int userId = 2;
        userRepository.deleteUserById(userId);

        ResultSet rs = statement.executeQuery(selectUsersQuery);
        List<UserDto> userDtos = new ArrayList<>();
        while (rs.next()) {
            UserDto userDto = UserDto.builder()
                    .id(rs.getInt("id"))
                    .login(rs.getString("login"))
                    .password(rs.getString("password"))
                    .build();
            userDtos.add(userDto);
        }
        statement.close();

        assertNull(findUserById(userId, userDtos));
    }

}
