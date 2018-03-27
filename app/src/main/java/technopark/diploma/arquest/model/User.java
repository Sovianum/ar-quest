package technopark.diploma.arquest.model;

import java.io.Serializable;

public class User implements Serializable {
    private String login;
    private String password;
    private int id;

    public User() {}

    public User(String login, String sex, String about, int age, int id) {
        this.login = login;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

