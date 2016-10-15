package com.csx.dao;

import com.csx.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by csx on 2016/7/2.
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type,type_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{data},#{createdDate},#{type},#{typeId})"})
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("types") List<Integer> types,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    List<Feed> selectMyFeeds(@Param("maxId") int maxId,
                               @Param("typeId") int typeId,
                               @Param("types") List<Integer> types,
                               @Param("offset") int offset,
                               @Param("limit") int limit);
}
