package com.android.tongzhiyuan.bean;

import java.io.Serializable;

/**
 * Gool Lee
 */
public class WageDailyInfo implements Serializable {


    private int deptId;
    private int pieceWageId;
    private String workDate;
    private String deptName;
    private String projectName;
    private double totalHourNum;
    private double totalHourlyWage;
    private double totalPieceNum;
    private double totalPieceWage;
    private double totalDeduction;
    private double totalWage;
    private int peopleNum;
    private String unit;

    //小料包
    private String pieceMonthCategoryName;
    private double totalPieceMonthNum;

    public String getPieceMonthCategoryName() {
        return pieceMonthCategoryName;
    }

    public void setPieceMonthCategoryName(String pieceMonthCategoryName) {
        this.pieceMonthCategoryName = pieceMonthCategoryName;
    }

    public double getTotalPieceMonthNum() {
        return totalPieceMonthNum;
    }

    public void setTotalPieceMonthNum(double totalPieceMonthNum) {
        this.totalPieceMonthNum = totalPieceMonthNum;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getPieceWageId() {
        return pieceWageId;
    }

    public void setPieceWageId(int pieceWageId) {
        this.pieceWageId = pieceWageId;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public double getTotalHourNum() {
        return totalHourNum;
    }


    public double getTotalHourlyWage() {
        return totalHourlyWage;
    }


    public double getTotalPieceNum() {
        return totalPieceNum;
    }


    public double getTotalPieceWage() {
        return totalPieceWage;
    }


    public double getTotalDeduction() {
        return totalDeduction;
    }


    public double getTotalWage() {
        return totalWage;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
