package com.csx.util;

import java.util.TreeSet;

/**
 * Created by csx on 2016/10/4.
 */
public interface IRedisOperator {
    /**
     * 根据pattern 获取所有的keys
     * @param pattern
     * @return
     */
    TreeSet<String> keys(String pattern);
}
