package com.i46.management.model.entity;


public enum AccessType {
    WRITE_ONCE_READ_ONCE(1),
    WRITE_ONCE_READ_MANY(2),
    APPEND_MANY_READ_MANY(3),
    WRITE_MANY_READ_MANY(4);

    // Enum can have methods
    private final int id;

    // Enum constructors are implicitly private
    private AccessType(int id) {
        this.id = id;
    }

    // Enum can have methods
    public int getId() {
        return this.id;
    }

}