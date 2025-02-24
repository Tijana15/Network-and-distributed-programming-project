package org.unibl.etf.bibliotekaklijent.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String lastname;
    private String address;
    private String email;
    private String username;
    private String password;
    private Boolean activated;

    public User() {

    }

    public User(String name, String lastname, String address, String email, String username, String password, Boolean activated) {
        super();
        this.name = name;
        this.lastname = lastname;
        this.address = address;
        this.email = email;
        this.username = username;
        this.password = password;
        this.activated = activated;
    }

    public User(String name, String lastname, String address, String email, String username, String password) {
        super();
        this.name = name;
        this.lastname = lastname;
        this.address = address;
        this.email = email;
        this.username = username;
        this.password = password;
        this.activated = false;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(username, other.username);
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", lastname=" + lastname + ", address=" + address + ", email=" + email
                + ", username=" + username + ", password=" + password + "]";
    }

}
