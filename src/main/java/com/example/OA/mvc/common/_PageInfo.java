package com.example.OA.mvc.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by aa on 2017/12/1.
 * PageInfo 插件 要在 mybatis 中使用，才会生效。对于工作流无法使用该插件，所有使用_PageInfo类似分页信息
 */
public class _PageInfo<T extends Serializable> implements Serializable{

    private int pageNum;
    private int pageSize;
    private int pages;
    private int total;
    private List<T> list;

    public _PageInfo(){}

    public _PageInfo(int pageNum , int pageSize , List<T> list)
    {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = list.size();
        this.pages = (int)Math.ceil((total*1.0)/pageSize);
        int start = (pageNum - 1)*pageSize;
        if(start >= total)
        {
            this.list = null;
        }else{
            if((start + pageSize ) <= total )
            {
                this.list = list.subList(start,start + pageSize) ;
            }else{
                this.list = list.subList(start,total);
            }
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
