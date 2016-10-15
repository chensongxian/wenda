package com.csx.service;

import com.csx.dao.TopicDAO;
import com.csx.model.Topic;
import com.csx.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by csx on 2016/9/15.
 */
@Service
public class TopicService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    TopicDAO topicDAO;


    public int addTopic(User user){
        return topicDAO.addTopic(user);
    }


    public Topic getTopicById(int topic_id){
        return topicDAO.selectById(topic_id);
    }

    public List<Topic> getTopicByLike(String like){
        return topicDAO.selectByLike(like);
    }
}
