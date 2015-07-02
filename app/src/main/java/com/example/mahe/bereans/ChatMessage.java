package com.example.mahe.bereans;

/**
 * Created by mahe on 20/6/15.
 */
public class ChatMessage {
    public boolean left;
    public String message;
    public String name;

    public ChatMessage(boolean left, String message, String name) {
        super();
        this.left = left;
        this.message = message;
        this.name = name;
    }
}