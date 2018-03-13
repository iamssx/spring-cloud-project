package com.ssx.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class AppPicUrl implements Serializable {

    @Id
    @NotNull
    private String url;
    @NotNull
    @ManyToOne
    @JoinColumn(name="aid")
    private App app;

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
