package com.csx.async.handler;

import com.csx.async.EventHandler;
import com.csx.async.EventModel;
import com.csx.async.EventType;
import com.csx.service.CommentService;
import com.csx.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by csx on 2016/8/28.
 */
@Component
public class AddCommentHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(AddCommentHandler.class);
    @Autowired
    SearchService searchService;

    @Override
    public void doHandle(EventModel model) {
        try {
            searchService.indexComment(model.getEntityOwnerId(),model.getExt("comment_content"));
        } catch (Exception e) {
            logger.error("增加评论索引失败");
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);
    }
}
