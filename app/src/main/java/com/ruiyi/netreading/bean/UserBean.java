package com.ruiyi.netreading.bean;

//用户登录模型
public class UserBean {
    private String username; //账号
    private String password; //密码
    private boolean ismemory; //记住密码
    private int terminal; //1pc、2pad、3移动

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsmemory() {
        return ismemory;
    }

    public int getTerminal() {
        return terminal;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIsmemory(boolean ismemory) {
        this.ismemory = ismemory;
    }

    public void setTerminal(int terminal) {
        this.terminal = terminal;
    }
}
