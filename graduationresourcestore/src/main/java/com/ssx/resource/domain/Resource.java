package com.ssx.resource.domain;

import com.ssx.common.domain.BaseDomain;
import com.ssx.common.domain.User;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})},
        indexes = {
                @Index(unique = true, name = "uk_name", columnList = "name")
        })
@Data
public class Resource implements Serializable, BaseDomain<Long> {
    @Id
    @GeneratedValue
    private Long rid;
    @NotNull
    @Column(length = 20)
    private String name;
    @NotNull
    private Long price; //1代表一分
    @NotNull
    private String description;
    @NotNull
    private String url;
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;
    @NotNull
    private String uuid;
    @NotNull
    @Column(length = 20)
    private String type;

    @Override
    public Long getID() {
        return getRid();
    }
}
