package com.csx.service;

import com.csx.dao.FeedDAO;
import com.csx.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by csx on 2016/8/12.
 */
@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds,List<Integer> types,int offset ,int limit) {
        return feedDAO.selectUserFeeds(maxId, userIds,types,offset,limit);
    }

    public List<Feed> getMyFeeds(int maxId, int typeId,List<Integer> types,int offset ,int limit) {
        return feedDAO.selectMyFeeds(maxId, typeId,types,offset,limit);
    }

    public boolean addFeed(Feed feed) {
        System.out.println(feed.getData());
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    public Feed getById(int id) {
        return feedDAO.getFeedById(id);
    }
}
