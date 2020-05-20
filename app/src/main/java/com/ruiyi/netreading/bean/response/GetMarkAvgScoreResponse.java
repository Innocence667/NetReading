package com.ruiyi.netreading.bean.response;

public class GetMarkAvgScoreResponse {

    /**
     * number : 16
     * subNumber : 0
     * avgScore : 5.0
     * myAvgScore : 4.666666666666667
     * count : 15
     */

    private int number;
    private String subNumber;
    private double avgScore;
    private double myAvgScore;
    private int count;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSubNumber() {
        return subNumber;
    }

    public void setSubNumber(String subNumber) {
        this.subNumber = subNumber;
    }

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public double getMyAvgScore() {
        return myAvgScore;
    }

    public void setMyAvgScore(double myAvgScore) {
        this.myAvgScore = myAvgScore;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
