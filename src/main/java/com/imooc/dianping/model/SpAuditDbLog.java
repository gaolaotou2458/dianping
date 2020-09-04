package com.imooc.dianping.model;

import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "dianpingdb.sp_audit_db_log")
@Data
public class SpAuditDbLog {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * sql入参
     */
    @Column(name = "IN_PARAM")
    private String inParam;

    /**
     * sql开始执行时间
     */
    @Column(name = "START_TIME")
    private Date startTime;

    /**
     * sql执行结束时间
     */
    @Column(name = "END_TIME")
    private Date endTime;

    /**
     * 执行sql耗时
     */
    @Column(name = "COST_TIME")
    private Long costTime;

    /**
     * 执行结果，01：成功；02：失败
     */
    @Column(name = "RESUTL_TYPE")
    private String resutlType;

    /**
     * 错误内容
     */
    @Column(name = "ERROR_CONTENT")
    private String errorContent;

    /**
     * 备注
     */
    @Column(name = "REMARK")
    private String remark;

    /**
     * SQL内容
     */
    @Column(name = "SQL_CONTENT")
    private String sqlContent;

    /**
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取sql入参
     *
     * @return IN_PARAM - sql入参
     */
    public String getInParam() {
        return inParam;
    }

    /**
     * 设置sql入参
     *
     * @param inParam sql入参
     */
    public void setInParam(String inParam) {
        this.inParam = inParam;
    }

    /**
     * 获取sql开始执行时间
     *
     * @return START_TIME - sql开始执行时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置sql开始执行时间
     *
     * @param startTime sql开始执行时间
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取sql执行结束时间
     *
     * @return END_TIME - sql执行结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置sql执行结束时间
     *
     * @param endTime sql执行结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取执行sql耗时
     *
     * @return COST_TIME - 执行sql耗时
     */
    public Long getCostTime() {
        return costTime;
    }

    /**
     * 设置执行sql耗时
     *
     * @param costTime 执行sql耗时
     */
    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    /**
     * 获取执行结果，01：成功；02：失败
     *
     * @return RESUTL_TYPE - 执行结果，01：成功；02：失败
     */
    public String getResutlType() {
        return resutlType;
    }

    /**
     * 设置执行结果，01：成功；02：失败
     *
     * @param resutlType 执行结果，01：成功；02：失败
     */
    public void setResutlType(String resutlType) {
        this.resutlType = resutlType;
    }

    /**
     * 获取错误内容
     *
     * @return ERROR_CONTENT - 错误内容
     */
    public String getErrorContent() {
        return errorContent;
    }

    /**
     * 设置错误内容
     *
     * @param errorContent 错误内容
     */
    public void setErrorContent(String errorContent) {
        this.errorContent = errorContent;
    }

    /**
     * 获取备注
     *
     * @return REMARK - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取SQL内容
     *
     * @return SQL_CONTENT - SQL内容
     */
    public String getSqlContent() {
        return sqlContent;
    }

    /**
     * 设置SQL内容
     *
     * @param sqlContent SQL内容
     */
    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }
}