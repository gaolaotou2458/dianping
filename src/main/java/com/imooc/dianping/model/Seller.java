package com.imooc.dianping.model;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "dianpingdb.seller")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "remark_score")
    private BigDecimal remarkScore;

    @Column(name = "disabled_flag")
    private Integer disabledFlag;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return created_at
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return updated_at
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return remark_score
     */
    public BigDecimal getRemarkScore() {
        return remarkScore;
    }

    /**
     * @param remarkScore
     */
    public void setRemarkScore(BigDecimal remarkScore) {
        this.remarkScore = remarkScore;
    }

    /**
     * @return disabled_flag
     */
    public Integer getDisabledFlag() {
        return disabledFlag;
    }

    /**
     * @param disabledFlag
     */
    public void setDisabledFlag(Integer disabledFlag) {
        this.disabledFlag = disabledFlag;
    }
}