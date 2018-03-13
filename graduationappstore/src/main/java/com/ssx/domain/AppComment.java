package com.ssx.domain;

import com.ssx.common.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class AppComment implements Serializable {

    @Id
    @GeneratedValue
    private Long cid;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "aid")
    private App app;
    @NotNull
    @OneToOne
    @JoinColumn(name = "uid")
    private User user;
    @NotNull
    @Column(length = 20)
    private String content;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
