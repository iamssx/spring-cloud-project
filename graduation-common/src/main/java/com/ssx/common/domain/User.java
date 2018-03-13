package com.ssx.common.domain;

import lombok.Data;

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
@Data
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

    @Override
    public Long getID() {
        return getUid();
    }
}
