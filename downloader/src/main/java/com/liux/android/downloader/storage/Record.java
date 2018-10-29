package com.liux.android.downloader.storage;

public class Record {
    private long id;
    private String url;
    private String headers;
    private String path;
    private String etag;
    private long completed;
    private long total;
    private int status;
    private long create_time;
    private long update_time;

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

    public String getPath() {
        return path;
    }

    public Record setPath(String path) {
        this.path = path;
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

    public long getCreate_time() {
        return create_time;
    }

    public Record setCreate_time(long create_time) {
        this.create_time = create_time;
        return this;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public Record setUpdate_time(long update_time) {
        this.update_time = update_time;
        return this;
    }
}
