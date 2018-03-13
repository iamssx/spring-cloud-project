package com.ssx.common.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(
        uniqueConstraints={@UniqueConstraint(columnNames={"username"})},
        indexes = {
                @Index(columnList = "username", unique = true, name = "uk_username")
        }
)
public class User implements BaseDomain<Long>, Serializable{

    @Id
    @NotNull
    @GeneratedValue
    private Long uid;

    @NotNull
    @Column(length = 20)
    private String username;
    @Column(length = 20)
    private String password;
    @Column(length = 20)
    private String src;
    private String srcId;

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
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

    @Override
    public Long getID() {
        return getUid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return uid != null ? uid.equals(user.uid) : user.uid == null;

    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }
}
