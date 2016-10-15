/**
 * Created by csx on 2016/9/15.
 */


$(function () {
    var oExports = {
        initialize: fInitialize,
        // 渲染更多数据
        renderMore: fRenderMore,
        // 请求数据
        requestData: fRequestData,
        // 简单的模板替换
        tpl: fTpl
    };
    // 初始化页面脚本
    oExports.initialize();

    function fInitialize() {
        var that = this;
        // 常用元素
        that.editEl = $('.zg-link-litblue-normal');





    }

    function fRenderMore(fCb) {
        var that = this;
        // 没有更多数据，不处理
        if (!that.listHasNext[that.topicId]) {
            $('.zu-button-more').html("没有数据");
            return;
        }
        that.requestData({
            topicId:that.topicId,
            offset: that.offset,
            limit: that.limit,
            call: function (oResult) {
                // 是否有更多数据
                that.listHasNext[that.topicId] = oResult.hasNext;
                // 更新当前页面
                that.offset+=that.limit;
                // 渲染数据
                var sHtml = '';
                $.each(oResult.data, function (nIndex, vo) {
                    var date =moment().format('YYYY-MM-DD HH:mm:ss',vo.question.createdDate);
                    sHtml += that.tpl([

                        '<div class="feed-item folding feed-item-hook feed-item-2" feed-item-a="" data-type="a" id="feed-2" data-za-module="FeedItem" data-za-index="">',
                        '<meta itemprop="ZReactor" data-id="389034" data-meta="{&quot;source_type&quot;: &quot;promotion_answer&quot;, &quot;voteups&quot;: 4168, &quot;comments&quot;: 69, &quot;source&quot;: []}">',

                        '<div class="feed-item-inner">',

                        '<div class="avatar">',
                        '<a title="'+vo.user.name+'" data-tip="p$t$amuro1230" class="zm-item-link-avatar" target="_blank" href="https://csx.com/people/amuro1230">',
                        '<img src="'+vo.user.headUrl+'" class="zm-item-img-avatar"/>','</a>',
                        '</div>',

                        '<div class="feed-main">',
                        '<div class="feed-content" data-za-module="AnswerItem">',
                        '<meta itemprop="answer-id" content="389034">',
                        '<meta itemprop="answer-url-token" content="13174385">',
                        '<h1 class="feed-title" style="color:#999999">',
                        '来自话题：','<a href="#" style="color:#999999">'+vo.topic.topic+'</a>',
                        '</h1>',
                        '<h2 class="feed-title">',
                        '<a class="question_link" target="_blank" href="/question/'+vo.question.id+'">'+vo.question.title+'</a>','</h2>',
                        '<div class="feed-question-detail-item">',
                        '<div class="question-description-plain zm-editable-content">','</div>',

                        '<div class="expandable entry-body">',
                        '<div class="zm-item-vote">',
                        '<a class="zm-item-vote-count js-expand js-vote-count" href="javascript:;" data-bind-votecount="">'+vo.followCount+'</a>','</div>',
                        '<div class="zm-item-answer-author-info">',

                        '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/user/'+vo.user.id+'">'+vo.user.name+'</a>'+
                        '，'+date+'</div>',
                        '<div class="zm-item-vote-info" data-votecount="4168" data-za-module="VoteInfo">',
                        '<span class="voters text">',
                        '<a href="#" class="more text">',
                        '<span class="js-voteCount">',4168,'</span>','&nbsp;人赞同','</a>','</span>',
                        '</div>',
                        '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                        '<div class="zh-summary summary clearfix">'+vo.question.content+'</div>',
                        '</div>',
                        '</div>',


                        '<div class="feed-meta">',
                        '<div class="zm-item-meta answer-actions clearfix js-contentActions">',
                        '<div class="zm-meta-panel">',
                        '<a data-follow="q:link" class="follow-link zg-follow meta-item" href="javascript:;" id="sfb-123114">',
                        '<i class="z-icon-follow">','</i>','关注问题','</a>',
                        '<a href="#" name="addcomment" class="meta-item toggle-comment js-toggleCommentBox">',
                        '<i class="z-icon-comment">','</i>'+vo.question.commentCount+'条评论'+'</a>',


                        '<button class="meta-item item-collapse js-collapse">',
                        '<i class="z-icon-fold">','</i>','收起','</button>',
                        '</div>',
                        '</div>',

                        '</div>',


                        '</div>',
                        '</div>',
                        '</div>',
                        '</div>',
                        '</div>',
                        '</div>',].join(''), vo);
                });
                sHtml && that.listEl.append(sHtml);
            },
            error: function () {
                alert('出现错误，请稍后重试');
            },
            always: fCb
        });
    }

    function fRequestData(oConf) {
        var that = this;
        var sUrl = '/loadMoreByTopicId/' +oConf.topicId+'/'+ oConf.offset + '/' + oConf.limit + '/';
        $.ajax({url: sUrl, dataType: 'json'}).done(oConf.call).fail(oConf.error).always(oConf.always);
    }

    function fTpl(sTpl, oData) {
        var that = this;
        sTpl = $.trim(sTpl);
        return sTpl.replace(/#{(.*?)}/g, function (sStr, sName) {
            return oData[sName] === undefined || oData[sName] === null ? '' : oData[sName];
        });
    }
});