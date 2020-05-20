package com.ruiyi.netreading.bean;

import java.util.List;

//分数面板模型
public class ScorePanel {

    private List<String> scores; //分数选项列表

    private List<Boolean> scoresCheck; //分数选中集合

    public List<String> getScores() {
        return scores;
    }

    public List<Boolean> getScoresCheck() {
        return scoresCheck;
    }

    public void setScores(List<String> scores) {
        this.scores = scores;
    }

    public void setScoresCheck(List<Boolean> scoresCheck) {
        this.scoresCheck = scoresCheck;
    }
}
