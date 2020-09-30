package com.android.tongzhiyuan.bean;

import java.io.Serializable;

/**
 * Gool
 */
public class EmplyeeWageInfo implements Serializable {

    private int id;
    private String createTime;
    private String updateTime;
    private int employeeId;
    private String workDate;
    private int pieceWageId;
    private String hourNum;
    private String pieceNum;
    private String pieceMonthNum;

    public String getPieceMonthNum() {
        return pieceMonthNum;
    }

    public void setPieceMonthNum(String pieceMonthNum) {
        this.pieceMonthNum = pieceMonthNum;
    }

    public double getTotalPieceMonthPrice() {
        return totalPieceMonthPrice;
    }

    public void setTotalPieceMonthPrice(double totalPieceMonthPrice) {
        this.totalPieceMonthPrice = totalPieceMonthPrice;
    }

    private double totalPieceMonthPrice;
    private String deduction;
    private String remark;
    private String projectName;
    private int creator;
    private String hourlyWage;
    private String pieceWage;
    private String attendReward;
    private String incomeTax;
    private String taxDeduction;
    private String employeeTypeStr;
    private String deptName;
    private String payableWage;
    private String taxAmount;
    private double realWage;
    private int deptId;
    private String employeeType;
    private String unit;
    private String employeeName;
    private double totalWage;

    private int wageYear;
    private int wageMonth;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public int getPieceWageId() {
        return pieceWageId;
    }

    public void setPieceWageId(int pieceWageId) {
        this.pieceWageId = pieceWageId;
    }

    public String getHourNum() {
        return hourNum;
    }

    public void setHourNum(String hourNum) {
        this.hourNum = hourNum;
    }

    public String getPieceNum() {
        return pieceNum;
    }

    public void setPieceNum(String pieceNum) {
        this.pieceNum = pieceNum;
    }

    public String getDeduction() {
        return deduction;
    }

    public void setDeduction(String deduction) {
        this.deduction = deduction;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(String hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public String getPieceWage() {
        return pieceWage;
    }

    public void setPieceWage(String pieceWage) {
        this.pieceWage = pieceWage;
    }

    public String getAttendReward() {
        return attendReward;
    }

    public void setAttendReward(String attendReward) {
        this.attendReward = attendReward;
    }

    public String getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(String incomeTax) {
        this.incomeTax = incomeTax;
    }

    public String getTaxDeduction() {
        return taxDeduction;
    }

    public void setTaxDeduction(String taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    public String getEmployeeTypeStr() {
        return employeeTypeStr;
    }

    public void setEmployeeTypeStr(String employeeTypeStr) {
        this.employeeTypeStr = employeeTypeStr;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPayableWage() {
        return payableWage;
    }

    public void setPayableWage(String payableWage) {
        this.payableWage = payableWage;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getRealWage() {
        return realWage;
    }

    public void setRealWage(double realWage) {
        this.realWage = realWage;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public double getTotalWage() {
        return totalWage;
    }

    public void setTotalWage(double totalWage) {
        this.totalWage = totalWage;
    }

    public int getWageYear() {
        return wageYear;
    }

    public void setWageYear(int wageYear) {
        this.wageYear = wageYear;
    }

    public int getWageMonth() {
        return wageMonth;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setWageMonth(int wageMonth) {
        this.wageMonth = wageMonth;
    }
}
