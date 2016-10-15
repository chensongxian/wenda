package com.csx.dao;

import com.csx.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by csx on 2016/7/2.
 */
@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    String INSET_FIELDS = " name, password, salt, head_url, email,sex,introduction,livePlace";
    String SELECT_FIELDS = " id, name, password, salt, head_url, email,sex,introduction,livePlace";

    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl},#{email})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);


    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where name like \"%\"#{like}\"%\" limit 0,5"})
    List<User> selectByLike(String like);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where name=#{name}"})
    User selectByName(String name);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where email=#{email}"})
    User selectByEmail(String email);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Update({"update ", TABLE_NAME, " set email=#{email} where id=#{id}"})
    void updateEmail(@Param("id") int id, @Param("email") String email);

    @Update({"update ", TABLE_NAME, " set head_url=#{headUrl} where id=#{id}"})
    void updateHeadUrl(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById(int id);

    @Update({"update ",TABLE_NAME," set name=#{name},sex=#{sex},introduction=#{introduction},livePlace=#{livePlace} where id=#{id}"})
    void updateUserInfo(User user);
}
