package com.ssx.resource.domain;

import com.ssx.common.domain.User;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
public class Comment implements Serializable {

    @Id
    @GeneratedValue
    private Long cid;
    @NotNull
    @ManyToOne
    @JoinColumn(name="uid")
    private User user;
    @NotNull
    @ManyToOne
    @JoinColumn(name="rid")
    private Resource resource;
    @NotNull
    @Column(length = 20)
    private String content;
}
