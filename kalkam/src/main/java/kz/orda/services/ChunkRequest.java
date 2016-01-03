package kz.orda.services;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by Ali on 03.01.2016.
 */
public class ChunkRequest implements Pageable {

    private int start;
    private int size;
    private Sort sort;

    public ChunkRequest(int start, int size) {
        this.start = start;
        this.size = size;
    }

    public ChunkRequest(int start, int size, Sort sort) {
        this.start = start;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public int getOffset() {
        return start;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return this;
    }

    @Override
    public Pageable first() {
        return this;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
