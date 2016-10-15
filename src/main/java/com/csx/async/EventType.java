package com.csx.async;

/**
 * Created by csx on 2016/7/30.
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5),
    ADD_QUESTION(6),
    QVIEW(7),
    DISLIKE(8),
    REG(9);

    private int value;
    EventType(int value) { this.value = value; }
    public int getValue() { return value; }
}
