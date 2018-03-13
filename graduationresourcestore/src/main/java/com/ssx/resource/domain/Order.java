package com.ssx.resource.domain;

import com.ssx.common.domain.BaseDomain;
import com.ssx.common.domain.User;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class Order implements BaseDomain<Long> {

    public static final byte UN_PAY = 0;
    public static final byte PAY = 1;

    @Id
    @NotNull
    private Long oid;
    @NotNull
    private Long time;
    @NotNull
    private Long count;
    @NotNull
    private Long amount;
    @NotNull
    @ManyToOne
    @JoinColumn(name="uid")
    private User user;
    @NotNull
    @ManyToOne
    @JoinColumn(name="rid")
    private Resource resource;
    @NotNull
    private Byte state; //0没付款， 1已付款

    @Override
    public Long getID() {
        return null;
    }
}
