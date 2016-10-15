package com.csx.async;

import com.alibaba.fastjson.JSONObject;
import com.csx.util.JedisSingleAdapter;
import com.csx.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by csx on 2016/7/30.
 */
@Service
public class EventProducer {
    @Autowired
    JedisSingleAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
