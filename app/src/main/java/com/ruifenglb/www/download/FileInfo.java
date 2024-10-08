package com.ruifenglb.www.download;

import java.io.Serializable;

/**
 * 作者：朱亮 on 2017/1/17 16:17
 * 邮箱：171422696@qq.com
 * 下载文件的详细信息(这里用一句话描述这个方法的作用)
 */
public class FileInfo implements Serializable {
    private int id;
    private String url;
    private String fileName;
    private int length;//总长度
    private int downlenth;//下载的长度
    private int downType = 0;//下载的状态(0、等待  1、开始下载  2、下载中  3、下载成功，4、下载失败，5、下载取消)
    private String picurl;

    public FileInfo() {
        super();
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", downlenth=" + downlenth +
                ", downType=" + downType +
                ", picurl=" + picurl +
                '}';
    }

    public FileInfo(int id, String url, String fileName, int length, int downlenth, int downType) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.downlenth = downlenth;
        this.downType = downType;
    }

    public FileInfo(int id, String url, String fileName, String picurl, int length, int downlenth, int downType) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.downlenth = downlenth;
        this.downType = downType;
        this.picurl = picurl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDownlenth() {
        return downlenth;
    }

    public void setDownlenth(int downlenth) {
        this.downlenth = downlenth;
    }

    public int getDownType() {
        return downType;
    }

    public void setDownType(int downType) {
        this.downType = downType;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }
}

