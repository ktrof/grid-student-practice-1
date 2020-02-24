package com.gridstudentpractice.chatservice.controller;

import com.gridstudentpractice.chatservice.model.Chatroom;
import com.gridstudentpractice.chatservice.service.ChatroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/restChat/chatroom")
public class ChatroomRestController {

    @Autowired
    private ChatroomService chatroomService;

    @PostMapping("/post")
    public void addChatroom(@Valid @RequestBody Chatroom chatroom) {
        chatroomService.createChatroom(chatroom);
    }

    @GetMapping("/get/byId/{id}")
    public Chatroom getChatroomById(@PathVariable int id) {
        return chatroomService.getChatroomById(id);
    }

    @GetMapping("/get/byName/{name}")
    public List<Chatroom> getChatroomNyName(@PathVariable String name) {
        return chatroomService.getChatroomByName(name);
    }
}
