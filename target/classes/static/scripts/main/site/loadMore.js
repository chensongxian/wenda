/**
 * Created by csx on 2016/9/12.
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
        that.listEl = $('#js-home-feed-list');
        // 初始化数据
        that.offset = 1;
        that.limit = 10;
        that.listHasNext = true;


        $(window).bind('scroll',function(){show()});

        function show()
        {

            if($(window).scrollTop()+$(window).height()>=$(document).height())
            {
                $('.zu-button-more').html("加载中.....");

                that.renderMore(function () {
                    // 没有数据隐藏加载更多按钮
                    !that.listHasNext && $('.zu-button-more').html("没有数据");;
                });
            }
        }
        // // 绑定事件
        // $('.zu-button-more').on('click', function (oEvent) {
        //     var oEl = $(oEvent.currentTarget);
        //     var sAttName = 'data-load';
        //     // 正在请求数据中，忽略点击事件
        //     if (oEl.attr(sAttName) === '1') {
        //         return;
        //     }
        //     // 增加标记，避免请求过程中的频繁点击
        //     oEl.attr(sAttName, '1');
        //     that.renderMore(function () {
        //         // 取消点击标记位，可以进行下一次加载
        //         oEl.removeAttr(sAttName);
        //         // 没有数据隐藏加载更多按钮
        //         !that.listHasNext && oEl.hide();
        //     });
        // });
    }

    function fRenderMore(fCb) {
        var that = this;
        // 没有更多数据，不处理
        if (!that.listHasNext) {
            return;
        }
        that.requestData({
            offset: that.offset,
            limit: that.limit,
            call: function (oResult) {
                // 是否有更多数据
                that.listHasNext = oResult.hasNext;
                // 更新当前页面
                that.offset+=that.limit;
                // 渲染数据
                var sHtml = '';
                $.each(oResult.data, function (nIndex, vo) {
                    var date =moment().format('YYYY-MM-DD HH:mm:ss',vo.question.createdDate);
                    var followLinkHtml='';
                    if(vo.followed){
                        followLinkHtml='<i class="z-icon-follow" data-status="1"></i>'+'取消关注'+'</a>';
                    }else {
                        followLinkHtml='<i class="z-icon-follow" data-status="1"></i>'+'关注问题'+'</a>';
                    }
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
                        '<a data-follow="q:link" class="follow-link zg-follow meta-item" href="javascript:;" id="sfb-123114" data-id="'+vo.question.id+'">',
                        followLinkHtml,
                        '<a href="#" name="addcomment" class="meta-item toggle-comment js-toggleCommentBox">',
                        '<i class="z-icon-comment">','</i>'+vo.commentCount+'条评论'+'</a>',


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
                console.log(sHtml);
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
        var sUrl = '/loadMore/' + oConf.offset + '/' + oConf.limit + '/';
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