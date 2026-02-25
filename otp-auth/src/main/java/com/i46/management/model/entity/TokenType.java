package com.i46.management.model.entity;


public enum TokenType {
    FILE_UPLOAD(1),
    ONE_TIME_KEYS(2),
    NEW_USER_ACCOUNT(3);

    // Enum can have methods
    private final int id;

    // Enum constructors are implicitly private
    private TokenType(int id) {
        this.id = id;
    }

    // Enum can have methods
    public int getId() {
        return this.id;
    }

}