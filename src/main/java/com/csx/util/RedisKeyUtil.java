package com.csx.util;

/**
 * Created by csx on 2016/7/30.
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    // 获取粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // 关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";


    private static String BIZ_NEWFEED = "NEWFEED";

    private static String BIZ_QVIEWS="QVIEWS";

    private static String BIZ_QUESTIONLIKE="QUESTIONLIKE";

    //热门提问用于打分
    private static String BIZ_QUSTIONHOT="QUESTIONHOT";

    //热门set存储了热门的问题
    private static String BIZ_QUESTIONHOTSET="QUESTIONHOTSET";

    //评论，打分
    private static String BIZ_COMMENTSORT="COMMENTSORT";

    //评论缓存
    private static String BIZ_COMMENTSET="COMMENTSET";

    //评论总数
    private static String BIZ_COMMENTCOUNT="COMMENTCOUNT";

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    // 某个实体的粉丝key
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    // 每个用户对某类实体的关注key
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getTimelineKey(int userId) {

        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }

    public static String getBizNewfeed(int userId) {

        return BIZ_NEWFEED + SPLIT + String.valueOf(userId);
    }

    public static String getBizQviews(int entityType, int entityId){
        return BIZ_QVIEWS + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getBizQuestionlike(int questionId){
        return BIZ_QUESTIONLIKE+SPLIT+String.valueOf(questionId);
    }

    public static String getBizQustionhot() {
        return BIZ_QUSTIONHOT;
    }

    public static String getBizQuestionhotset(String questionId){
        return BIZ_QUESTIONHOTSET +SPLIT+questionId;
    }


    public static String getBizCommentsort(String questionId){
        return BIZ_COMMENTSORT+SPLIT+String.valueOf(questionId);
    }

    public static String getBizCommentset(String commentId){
        return BIZ_COMMENTSET+SPLIT+String.valueOf(commentId);
    }

    public static String getBizCommentcount(String questionId){
        return BIZ_COMMENTCOUNT+SPLIT+questionId;
    }
}
