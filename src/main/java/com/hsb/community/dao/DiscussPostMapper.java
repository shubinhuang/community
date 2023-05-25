package com.hsb.community.dao;

import com.hsb.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //  分页，起始行号和每页行数
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在动态标签<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);
}
