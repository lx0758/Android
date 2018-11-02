package com.liux.android.downloader.storage;

/**
 * 下载信息记录实体
 */
public class Record {

    // 任务ID
    private long id;
    // 任务URL
    private String url;
    // 任务请求方法
    private String method;
    // 任务请求头
    private String headers;
    // 任务存储目录
    private String dir;
    // 任务存储文件名
    private String fileName;
    // 任务实际存储文件名(已生成文件,且可能与 fileName 不同)
    private String fileNameFinal;
    // 任务已记录的 ETAG 缓存标志
    private String etag;
    // 任务已完成量
    private long completed;
    // 任务总量
    private long total;
    // 任务状态
    private int status;
    // 任务创建时间
    private long createTime;
    // 任务最后更新时间
    private long updateTime;
    // 是否是单任务模式
    private boolean single;

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

    public String getFileNameFinal() {
        return fileNameFinal;
    }

    public Record setFileNameFinal(String fileNameFinal) {
        this.fileNameFinal = fileNameFinal;
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

    public boolean getSingle() {
        return single;
    }

    public Record setSingle(boolean single) {
        this.single = single;
        refreshUpdateTime();
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
                ", fileNameFinal='" + fileNameFinal + '\'' +
                ", etag='" + etag + '\'' +
                ", completed=" + completed +
                ", total=" + total +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", single=" + single +
                '}';
    }

    private void refreshUpdateTime() {
        this.updateTime = System.currentTimeMillis();
    }
}
