package com.zebrunner.reporting.service.util;

import java.util.concurrent.TimeUnit;

/**
 * JenkinsConfig - configures Jenkins credentials and timeouts.
 * 
 * @author akhursevich
 */
public class JenkinsConfig {

    private int timeout = 15;
    private String username;
    private String password;

    public JenkinsConfig(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public JenkinsConfig(String username, String password, int timeout) {
        this.timeout = (int) TimeUnit.SECONDS.toMillis(timeout);
        this.username = username;
        this.password = password;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}