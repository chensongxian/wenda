package com.csx.service;

import org.springframework.stereotype.Service;

/**
 * Created by csx on 2016/7/10.
 */
@Service
public class WendaService {
    public String getMessage(int userId) {
        return "Hello Message:" + String.valueOf(userId);
    }
}
