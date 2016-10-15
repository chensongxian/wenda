package com.csx;

import com.alibaba.fastjson.JSON;
import com.csx.model.*;
import com.csx.service.*;
import com.csx.util.*;
import com.qiniu.util.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@WebAppConfiguration
public class WendaApplicationTests {

	@Autowired
	MailSender mailSender;
	@Autowired
	JedisSingleAdapter jedisAdapter;

	@Autowired
	TopicService topicService;

	@Autowired
	QuestionService questionService;

	@Autowired
	QiniuService qiniuService;

	@Autowired
	FollowService followService;

	@Autowired
	SearchService searchService;

	@Autowired
	LikeService likeService;

	@Autowired
	CommentService commentService;
	@Test
	public void testEmail(){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("username","csx");
		mailSender.sendWithHTMLTemplate("1164077611@qq.com", "登陆IP异常", "mails/login_exception.html", map);
	}

	@Test
	public void testSetCode(){
//		jedisAdapter.setEx("w7SrhLuf20",60,"chen@qq.com");
		System.out.println(jedisAdapter.getEx("xCNUelAT20"));
	}

	@Test
	public void testGetCode(){
		System.out.println(WendaUtil.MD5("newpasswordcbee6"));
	}
	@Test
	public void contextLoads() {
	}

	@Test
	public void testCode(){
		System.out.println(RandomCode.randomCode(5));
	}

	@Test
	public void testTopic(){
		List<Topic> topics=topicService.getTopicByLike("美");
		System.out.println("size:"+topics.size());
		for (Topic topic: topics){
			System.out.println("-------"+topic.getTopic());
		}
	}

	@Test
	public void testTopic_1(){
		Question question=questionService.getById(2);
//		Topic topic=topicService.getTopicById(question.getTopic_id());
		System.out.println("话题:"+question.getTopicId());

	}


	@Test
	public void test() throws IOException {
		File file=new File("G:\\idea\\wenda\\src\\main\\resources\\static\\images\\avatar.png");
		int l = (int) (file.length());
		FileInputStream fis = null;
		byte[] src = new byte[l];
		fis = new FileInputStream(file);
		fis.read(src);
		String file64 = Base64.encodeToString(src, 0);

		String url=qiniuService.uploadBase64(file64);
		System.out.println(url);
	}

	@Test
	public void testFollow(){
		boolean ret = followService.follow(2, EntityType.ENTITY_QUESTION, 354);
	}


	@Test
	public void testIncr(){
//		jedisAdapter.sadd("test","1");
//		jedisAdapter.sadd("test","2");
//		jedisAdapter.set("new","0");
		String qViewKey=RedisKeyUtil.getBizQviews(EntityType.ENTITY_QUESTION,359);
		System.out.println(+jedisAdapter.scard(qViewKey));
	}

	@Test
	public void testSearch()  {
		try {
			List<SearchResult> list=searchService.searchContent("科技", 0, 10,
                    "<em>", "</em>");
			for(SearchResult result:list){
				if(result.getType()==EntityType.ENTITY_QUESTION){
					System.out.println("问题内容:"+result.getQuestion().getContent());
				}else if(result.getType()==EntityType.ENTITY_COMMENT){
					System.out.println("评论内容:"+result.getComment().getContent());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSearchUser(){
		try {
			List<User> list=searchService.searchUser("USER",0,10);
			for(User user:list){
				System.out.println(user.getName());
			}
		}catch (Exception e){

		}
	}

	@Test
	public void insertScore(){
		List<Question> questionList=questionService.getLatestQuestions(0,0,328);
		for(Question question:questionList){

//			boolean ret = followService.follow(question.getUserId(), EntityType.ENTITY_QUESTION, question.getId());


			long qView=questionService.getQViewKey(EntityType.ENTITY_QUESTION,question.getId());
			int qAnswers=question.getCommentCount();
			long qScore=followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId());
			long aScore=likeService.getQuestionLikeCount(question.getId());

			Date date_ask=question.getCreatedDate();

			Date date_active=null;
			if(qAnswers!=0) {
				List<Comment> comments=commentService.getCommentsByEntity(question.getId(),EntityType.ENTITY_QUESTION,0,1);
				date_active = comments.get(0).getCreatedDate();
			}else{
				date_active=date_ask;
			}
			double score=ScoreUtil.getScoreQuestion(qView,qAnswers,qScore,aScore,date_ask,date_active);

			System.out.println("打分:"+score);
			questionService.updateScore(question.getId(),score);
		}

	}

	//添加访问量
	@Test
	public void addQivew(){
		Random random=new Random();
		for(int i=1;i<357;i++) {
			int userId = random.nextInt(20);
			System.out.println(userId);
		}

//		questionService.qView(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,qid);
	}



	//初始化问题缓存
	@Test
	public void initCache(){
		List<Question> questionList=questionService.getLatestQuestionsByScoreNoCache(0,0,329);
		int i=0;
		for(Question question:questionList){
			System.out.println(question.getScore());
			jedisAdapter.zadd(RedisKeyUtil.getBizQustionhot(),question.getScore(),String.valueOf(question.getId()));

			String questionJson= JSON.toJSONString(question);
			jedisAdapter.set(RedisKeyUtil.getBizQuestionhotset(String.valueOf(question.getId())),questionJson);
			i++;
		}
		System.out.println("总数:"+i);

	}

	@Test
	public void initCommentCount(){
		List<Question> questionList=questionService.getLatestQuestionsByScoreNoCache(0,0,331);
		int i=0;
		for(Question question:questionList){
			jedisAdapter.set(RedisKeyUtil.getBizCommentcount(String.valueOf(question.getId())),String.valueOf(question.getCommentCount()));
			i++;
		}
		System.out.println("总数:"+i);

	}

	@Test
	public void rankTest(){

//		Double score=jedisAdapter.zscore(RedisKeyUtil.getBizQustionhot(),"13");
//		System.out.println(score);
		Set<String> set=jedisAdapter.zrevrange(RedisKeyUtil.getBizQustionhot(),9,10);
		for(String str:set){
			String json=jedisAdapter.get(RedisKeyUtil.getBizQuestionhotset(str));
			Question question=JSON.parseObject(json,Question.class);
			System.out.println(question.getContent());
		}
	}

	@Test
	public void testCommentScore(){
		List<Comment> comments=commentService.selectAll();
		for(Comment comment:comments){
			int questionId=comment.getEntityId();
			long likeCount=likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId());
			long disLikeCount=likeService.getDisLikeCount(EntityType.ENTITY_COMMENT,comment.getId());

			double score=ScoreUtil.getScoreComment(likeCount,disLikeCount);

			String commentJson=JSON.toJSONString(comment);

			String key=RedisKeyUtil.getBizCommentsort(String.valueOf(comment.getEntityId()));
			jedisAdapter.zadd(key,score,String.valueOf(comment.getId()));


			jedisAdapter.set(RedisKeyUtil.getBizCommentset(String.valueOf(comment.getId())),commentJson);
		}
	}


	//测试删除问题缓存
	@Test
	public void testUpdateQuestionScore(){
		int count= (int) jedisAdapter.zcard(RedisKeyUtil.getBizQustionhot());
		Set<String> set=jedisAdapter.zrange(RedisKeyUtil.getBizQustionhot(),0,count);
		int i=0;
		for(String str:set){
//			System.out.println(str);
			int id=Integer.parseInt(str);
			double score=jedisAdapter.zscore(RedisKeyUtil.getBizQustionhot(),str);
			System.out.println(score);
//			questionService.updateScore(id,score);
		}
		System.out.println(count);
	}


	@Test
	public void testDelQuestionCache(){
		int count= (int) jedisAdapter.zcard(RedisKeyUtil.getBizQustionhot());
		Set<String> set=jedisAdapter.zrange(RedisKeyUtil.getBizQustionhot(),0,count);
		int i=0;
		for(String str:set){
//			System.out.println(str);
			int id=Integer.parseInt(str);
			String json=jedisAdapter.get(RedisKeyUtil.getBizQuestionhotset(str));
			if(json!=null) {
				jedisAdapter.srem(RedisKeyUtil.getBizQuestionhotset(str), json);
			}
			jedisAdapter.zrem(RedisKeyUtil.getBizQustionhot(),str);
//			System.out.println(score);
		}
		System.out.println(count);
	}

	@Test
	public void testDelAll(){
		boolean flag=jedisAdapter.delAll(RedisKeyUtil.getBizQustionhot());
		System.out.println(flag);
	}


	@Test
	public void updateCommentScore(){

		Set<String> set=jedisAdapter.getAll(RedisKeyUtil.getBizCommentsort(""));
		int i=0;
		for(String key:set){
//			System.out.println(key);
			int count= (int) jedisAdapter.zcard(key);
//			System.out.println(count);
			Set<String> idSet=jedisAdapter.zrange(key,0,count);
			for(String str:idSet){
				int id=Integer.parseInt(str);
				double score=jedisAdapter.zscore(key,str);
				System.out.println("id:"+id+"--score:"+score);
//				commentService.updateScore(id,score);
				i++;
			}
		}
		System.out.println("总数:"+i);
	}


	@Test
	public void delCache(){
		boolean isDelQuestionHotSet=jedisAdapter.delAll(RedisKeyUtil.getBizQuestionhotset(""));

		boolean isDelQuestionHot=jedisAdapter.delAll(RedisKeyUtil.getBizQustionhot());


		boolean isDelCommentHotSet=jedisAdapter.delAll(RedisKeyUtil.getBizCommentset(""));


		boolean isDelCommentHot=jedisAdapter.delAll(RedisKeyUtil.getBizCommentsort(""));


	}


}
