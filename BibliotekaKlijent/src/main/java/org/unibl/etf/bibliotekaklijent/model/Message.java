package org.unibl.etf.bibliotekaklijent.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Message implements Comparable<Message>, Serializable {
    private String fromUser;
    private String toUser;
    private String message;
    private Date date;

    public Message() {
        super();
    }

    public Message(String fromUser, String toUser, String message, Date date) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
        this.date = date;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(date, message.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }

    @Override
    public int compareTo(Message other) {
        return this.date.compareTo(other.date);
    }
}
