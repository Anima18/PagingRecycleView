package com.anima.paginglistview;

/**
 * Created by jianjianhong on 18-12-26
 */
public class PagingStatus {

    public Status status;
    public String msg;
    public final static PagingStatus LOADED = new PagingStatus(Status.SUCCESS, null);
    public final static PagingStatus LOGING = new PagingStatus(Status.RUNNING, null);

    public PagingStatus(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static PagingStatus error(String msg) {
        return new PagingStatus(Status.FAILED, msg);
    }

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }
}
