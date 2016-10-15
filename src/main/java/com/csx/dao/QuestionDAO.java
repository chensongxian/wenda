package com.csx.dao;

import com.csx.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by csx on 2016/7/2.
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, user_id,created_date, comment_count,topic_id,score";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{userId},#{createdDate},#{commentCount},#{topicId},0)"})
    int addQuestion(Question question);



    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    List<Question> selectQuestionsByScore(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);


    List<Question> selectLatestQuestionsByTopicId(@Param("topicId") int topicId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Question getById(int id);



    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    @Update({"update ", TABLE_NAME," set score = #{score} where id=#{id}"})
    int updateScore(@Param("id") int id,@Param("score") double score);


}
