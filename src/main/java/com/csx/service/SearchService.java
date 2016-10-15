package com.csx.service;

import com.csx.model.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by csx on 2016/8/28.
 */
@Service
public class SearchService {
    private static final String SOLR_URL = "http://119.29.223.150:8983/solr/wenda";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();

    private static final String QUESTION_ID_FIELD = "question_id";
    private static final String QUESTION_TITLE_FIELD = "question_title";
    private static final String QUESTION_CONTENT_FIELD = "question_content";

    private static final String COMMENT_ID_FIELD = "comment_id";
    private static final String COMMENT_CONTENT_FIELD="comment_content";

    private static final String USER_ID_FIELD = "user_id";
    private static final String USER_NAME_FIELD="user_name";

    public List<SearchResult> searchContent(String keyword, int offset, int count,
                                            String hlPre, String hlPos) throws Exception {
        List<SearchResult> resultList = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        //高亮字符串限制，如果不设置可能会字符串显示不完整
        query.setHighlightFragsize(100000);
        query.set("hl.fl", QUESTION_TITLE_FIELD + "," + QUESTION_CONTENT_FIELD+","+COMMENT_CONTENT_FIELD);
        QueryResponse response = client.query(query);
        Map<String,Map<String,List<String>>> highlighting=response.getHighlighting();
        for(SolrDocument doc:response.getResults()){

            SearchResult searchResult=new SearchResult();
            if(doc.containsKey(QUESTION_ID_FIELD)) {
                //设置类型
                System.out.println(doc.get(QUESTION_ID_FIELD));
                searchResult.setType(EntityType.ENTITY_QUESTION);

                Question question=new Question();
                int id= Integer.parseInt(String.valueOf(doc.getFirstValue(QUESTION_ID_FIELD)));
                question.setId(id);
                List<String> questionContentlist=highlighting.get(doc.get("id")).get(QUESTION_CONTENT_FIELD);
                if(questionContentlist!=null){
                    question.setContent(questionContentlist.get(0));
                }
                List<String> questionTitlelist=highlighting.get(doc.get("id")).get(QUESTION_TITLE_FIELD);
                if(questionTitlelist!=null){
                    question.setTitle(questionTitlelist.get(0));
                }
                searchResult.setQuestion(question);
            }else if(doc.containsKey(COMMENT_ID_FIELD)){
                //设置类型
                searchResult.setType(EntityType.ENTITY_COMMENT);
                Comment comment=new Comment();
                int id= Integer.parseInt(String.valueOf(doc.getFirstValue(COMMENT_ID_FIELD)));
                comment.setId(id);
                List<String> commentContentList=highlighting.get(doc.get("id")).get(COMMENT_CONTENT_FIELD);

                if(commentContentList!=null){
//                    System.out.println("评论:"+doc.getFirstValue(COMMENT_CONTENT_FIELD));
//                    System.out.println("------------------");
                    comment.setContent(commentContentList.get(0));
                }
                searchResult.setComment(comment);
            }
            resultList.add(searchResult);
        }

        return resultList;
    }



    public List<User> searchUser(String keyword, int offset, int count) throws Exception {
        List<User> userList = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        query.setRows(count);
        query.setStart(offset);
        query.set("df","user_name");

        QueryResponse response = client.query(query);
        for (SolrDocument doc:response.getResults()){
            User user=new User();
            if(doc.containsKey(USER_ID_FIELD)){
                int id=Integer.parseInt(String.valueOf(doc.getFirstValue(USER_ID_FIELD)));
                user.setId(id);
            }

            if(doc.containsKey(USER_NAME_FIELD)){
                user.setName(String.valueOf(doc.getFirstValue(USER_NAME_FIELD)));
            }
            userList.add(user);
        }
        return userList;
    }

    public boolean indexQuestion(int qid, String title, String content) throws Exception {
        SolrInputDocument doc =  new SolrInputDocument();
        doc.setField(QUESTION_ID_FIELD, qid);
        doc.setField(QUESTION_TITLE_FIELD, title);
        doc.setField(QUESTION_CONTENT_FIELD, content);
        UpdateResponse response = client.add(doc, 1000);
        return response != null && response.getStatus() == 0;
    }


    public boolean indexComment(int cid, String content) throws Exception {
        SolrInputDocument doc =  new SolrInputDocument();
        doc.setField(COMMENT_ID_FIELD, cid);
        doc.setField(COMMENT_CONTENT_FIELD, content);
        UpdateResponse response = client.add(doc, 1000);
        return response != null && response.getStatus() == 0;
    }

    public boolean indexUser(int uid, String name) throws Exception {
        SolrInputDocument doc =  new SolrInputDocument();
        doc.setField(USER_ID_FIELD, uid);
        doc.setField(USER_NAME_FIELD, name);
        UpdateResponse response = client.add(doc, 1000);
        return response != null && response.getStatus() == 0;
    }

}
