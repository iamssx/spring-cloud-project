package com.ssx.domain;

import com.ssx.common.domain.BaseDomain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        indexes = {
                @Index(columnList = "name", unique = true, name = "uk_name")
        }
)
public class App implements Serializable, BaseDomain<Long> {

    @Id
    @GeneratedValue
    private Long aid;
    @Column(length = 20, nullable = false)
    private String name;
    @NotNull
    private String description; //描述
    @Column(length = 20, nullable = false)
    private String appType; //类型
    @NotNull
    private Short score; //评分,后台百分制，显示时10分制
    @NotNull
    private Long totalPeople; //总评分人数
    @NotNull
    private Date updateDate; //更新日期
    @Column(length = 20, nullable = false)
    private String provider; //提供者
    @NotNull
    private String osVersionRequest; //系统版本要求
    @NotNull
    private String fileUri;
    @NotNull
    private String uuid;

    @Override
    public Long getID() {
        return aid;
    }

    public Long getAid() {
        return aid;
    }

    public void setAid(Long aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Short getScore() {
        return score;
    }

    public void setScore(Short score) {
        this.score = score;
    }

    public Long getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(Long totalPeople) {
        this.totalPeople = totalPeople;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getOsVersionRequest() {
        return osVersionRequest;
    }

    public void setOsVersionRequest(String osVersionRequest) {
        this.osVersionRequest = osVersionRequest;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
