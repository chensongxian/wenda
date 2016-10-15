package com.csx.dao;

import com.csx.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by csx on 2016/7/2.
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ,score";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status},0)"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Comment getCommentById(int id);


    List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType,@Param("offset") int offset,
                                        @Param("limit") int limit);

    List<Comment> selectCommentByEntityAndScore(@Param("entityId") int entityId, @Param("entityType") int entityType,@Param("offset") int offset,
                                        @Param("limit") int limit);



    @Select({"select count(id) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update comment set status=#{status} where id=#{id}"})
    int updateStatus(@Param("id") int id, @Param("status") int status);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where user_id=#{userId} limit #{offset},#{limit}"})
    List<Comment> selectByUserId(@Param("userId") int userId,@Param("offset") int offset,@Param("limit") int limit);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME})
    List<Comment> selectAll();

    @Update({"update comment set score=#{score} where id=#{id}"})
    int updateScore(@Param("id") int id,@Param("score") double score);
}
