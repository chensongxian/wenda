package com.csx.util;

import java.util.Date;
import java.util.Random;

/**
 * 打分工具类
 * Created by csx on 2016/10/2.
 */
public class ScoreUtil {
    //获取问题的分值
    public static double getScoreQuestion(long qViews, int qAnswers, long qScore, long aScores, Date date_ask, Date date_active){

        Random random=new Random();
        qViews+=2;


//        System.out.println("qViews:"+qViews+"--qAnswers:"+qAnswers+"--qScore:"+qScore+"--aScores:"+aScores);
        Date dateNow=new Date();

        double qAge=(dateNow.getTime()-date_ask.getTime())/3600000;
        qAge=Math.round(qAge);
        double qUpdated=(dateNow.getTime()-date_active.getTime())/3600000;
        qUpdated=Math.round(qUpdated);


        double dividend=Math.log10(qViews)*4+(qAnswers*qScore)/5+aScores;

//        System.out.println("分子:"+dividend);

        double divisor=Math.pow(((qAge+1)-(qAge-qUpdated)/2),1.5);

//        System.out.println("分母:"+divisor);
        return dividend/divisor;

    }

    //获取评论的分值,使用基于威尔逊区间的算法，此算法也在reddit评论上使用
    public static double getScoreComment(long like,long dislike){
        long n=like+dislike;
        if(n==0){
            return 0;
        }

        double z=1.0;
        double phat=(double)like/n;
        return (phat+z*z/(2*n)-z*Math.sqrt((phat*(1-phat)+z*z/(4*n))/n))/(1+z*z/n);
    }



}
