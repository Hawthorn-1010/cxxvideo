package com.hzy.cxxvideo.utils;

import java.util.List;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo.utils
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/16 12:37
 **/
public class PageResult {

    // 当前页数
    private int page;
    // 总页数
    private int total;
    // 总记录数
    private long records;
    // 每行显示的内容
    private List<?> rows;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
