package com.bibmovel.client.object;

/**
 * Created by vinibrenobr11 on 11/08/18 at 10:38
 */
public class Account {

    private String user;
    private String email;

    public Account(String user, String email) {
        this.user = user;
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
