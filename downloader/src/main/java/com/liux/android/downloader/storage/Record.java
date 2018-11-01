package com.liux.android.downloader.storage;

/**
 * 下载信息记录实体
 */
public class Record {

    private long id;
    private String url;
    private String method;
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
        refreshUpdateTime();
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Record setMethod(String method) {
        this.method = method;
        refreshUpdateTime();
        return this;
    }

    public String getHeaders() {
        return headers;
    }

    public Record setHeaders(String headers) {
        this.headers = headers;
        refreshUpdateTime();
        return this;
    }

    public String getDir() {
        return dir;
    }

    public Record setDir(String dir) {
        this.dir = dir;
        refreshUpdateTime();
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public Record setFileName(String fileName) {
        this.fileName = fileName;
        refreshUpdateTime();
        return this;
    }

    public String getEtag() {
        return etag;
    }

    public Record setEtag(String etag) {
        this.etag = etag;
        refreshUpdateTime();
        return this;
    }

    public long getCompleted() {
        return completed;
    }

    public Record setCompleted(long completed) {
        this.completed = completed;
        refreshUpdateTime();
        return this;
    }

    public long getTotal() {
        return total;
    }

    public Record setTotal(long total) {
        this.total = total;
        refreshUpdateTime();
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Record setStatus(int status) {
        this.status = status;
        refreshUpdateTime();
        return this;
    }

    public long getCreateTime() {
        return createTime;
    }

    public Record setCreateTime(long createTime) {
        this.createTime = createTime;
        refreshUpdateTime();
        return this;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public Record setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
        //refreshUpdateTime();
        return this;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headers='" + headers + '\'' +
                ", dir='" + dir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", etag='" + etag + '\'' +
                ", completed=" + completed +
                ", total=" + total +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    private void refreshUpdateTime() {
        this.updateTime = System.currentTimeMillis();
    }
}
