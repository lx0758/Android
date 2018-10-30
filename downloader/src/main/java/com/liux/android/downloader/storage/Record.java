package com.liux.android.downloader.storage;

/**
 * 下载信息记录实体
 */
public class Record {

    private long id;
    private String url;
    private String headers;
    private String dir;
    private String fileName;
    private String etag;
    private long completed;
    private long total;
    private int status;
    private long createTime;
    private long updateTime;

    public long getId() {
        return id;
    }

    public Record setId(long id) {
        this.id = id;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Record setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getHeaders() {
        return headers;
    }

    public Record setHeaders(String headers) {
        this.headers = headers;
        return this;
    }

    public String getDir() {
        return dir;
    }

    public Record setDir(String dir) {
        this.dir = dir;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public Record setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getEtag() {
        return etag;
    }

    public Record setEtag(String etag) {
        this.etag = etag;
        return this;
    }

    public long getCompleted() {
        return completed;
    }

    public Record setCompleted(long completed) {
        this.completed = completed;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public Record setTotal(long total) {
        this.total = total;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Record setStatus(int status) {
        this.status = status;
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public Record setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public Record setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
