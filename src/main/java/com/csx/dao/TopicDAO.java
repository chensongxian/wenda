package com.csx.dao;

import com.csx.model.Topic;
import com.csx.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * Created by csx on 2016/9/13.
 */
@Mapper
public interface TopicDAO {
    String TABLE_NAME = "topic";
    String INSET_FIELDS = " topic";
    String SELECT_FIELDS = " topic_id, topic";

    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{topic})"})
    int addTopic(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where topic_id=#{topicId}"})
    Topic selectById(int topicId);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where topic like \"%\"#{title}\"%\""})
    List<Topic> selectByLike(String like);
}
