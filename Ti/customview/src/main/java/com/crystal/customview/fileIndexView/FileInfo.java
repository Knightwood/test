package com.crystal.customview.fileIndexView;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 6:53
 */
public class FileInfo {
    private int imgId;
    private String fileName;
    private String fileSize;
    private String filePath;
    private String fileCreateDate;


    public FileInfo(int imgId,String fileName,String path) {
        this(fileName,path);
        this.imgId=imgId;
    }
    public FileInfo(String fileName,String path, String fileSize) {
        this(fileName,path);
        this.fileSize = fileSize;
    }
    public FileInfo(String fileName,String path){
        super();
        this.fileName = fileName;
        this.filePath=path;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileCreateDate() {
        return fileCreateDate;
    }

    public void setFileCreateDate(String fileCreateDate) {
        this.fileCreateDate = fileCreateDate;
    }

}
