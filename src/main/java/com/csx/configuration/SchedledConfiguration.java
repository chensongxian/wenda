package com.csx.configuration;


import com.csx.quartz.ScheduledTasks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ParseException;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by csx on 2016/10/4.
 */
@Configuration
public class SchedledConfiguration {


    @Bean(name = "detailFactoryBean")
    public MethodInvokingJobDetailFactoryBean detailFactoryBean(ScheduledTasks scheduledTasks){
        MethodInvokingJobDetailFactoryBean bean = new MethodInvokingJobDetailFactoryBean ();
        //这儿设置对应的Job对象
        bean.setTargetObject (scheduledTasks);
        //这儿设置对应的方法名  与执行具体任务调度类中的方法名对应
        bean.setTargetMethod ("work");
        bean.setConcurrent (false);
        return bean;
    }

    @Bean(name = "cronTriggerBean")
    public CronTriggerFactoryBean cronTriggerBean(MethodInvokingJobDetailFactoryBean detailFactoryBean){
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean ();
        trigger.setJobDetail (detailFactoryBean.getObject ());
        try {
            //每天凌晨两点执行
            trigger.setCronExpression ("0 0 2 * * ?");
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        return trigger;

    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(CronTriggerFactoryBean cronTriggerBean){
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean ();
        schedulerFactory.setTriggers(cronTriggerBean.getObject());
        return schedulerFactory;
    }
}
