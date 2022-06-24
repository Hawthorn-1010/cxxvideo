package com.hzy.cxxvideo.mapper;

import com.hzy.cxxvideo.entity.SearchRecords;
import com.hzy.cxxvideo.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
    public List<String> getHotWords();
}