package com.csx.async;

import java.util.List;

/**
 * Created by csx on 2016/7/30.
 */
public interface EventHandler {
    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();
}
