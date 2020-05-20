package com.ruiyi.netreading.bean;

import java.util.List;

public class LocalImageData {

    private String path; //图片在本地的path
    private List<ImageData> list; //合并图片前，每个图片的宽高数据

    public String getPath() {
        return path;
    }

    public List<ImageData> getList() {
        return list;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setList(List<ImageData> list) {
        this.list = list;
    }
}
