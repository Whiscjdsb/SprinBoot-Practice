package com.example.springbootpractice.common;

import java.util.List;

public class PageResult<T> {
    private List<T> list;
    private Long total;
    private Long page;
    private Long size;
    private Long pages;

    public PageResult() {

    }

    public PageResult(List<T> list, Long total, Long page, Long size, Long pages) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = pages;
    }

    public static <T> PageResult<T> of(List<T> list, Long total, Long page, Long size, Long pages) {
        return new PageResult<>(list, total, page, size, pages);
    }

    public List<T> getList() {
        return list;
    }

    public Long getTotal() {
        return total;
    }

    public Long getPage() {
        return page;
    }

    public Long getSize() {
        return size;
    }

    public Long getPages() {
        return pages;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }
}
