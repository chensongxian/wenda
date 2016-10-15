/**
 * Created by csx on 2016/9/25.
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
        that.listEl = $('#js-profile-feed-list');
        // 初始化数据
        that.offset = 0;
        that.limit = 10;
        that.listHasNext = true;

        that.renderMore(function () {
            // 没有数据隐藏加载更多按钮
            !that.listHasNext&& $('#profile-load-more').html("没有数据");;
        });

        // 绑定事件
        $('#profile-load-more').on('click', function (oEvent) {
            var oEl = $(oEvent.currentTarget);
            var sAttName = 'data-load';
            // 正在请求数据中，忽略点击事件
            if (oEl.attr(sAttName) === '1') {
                return;
            }
            // 增加标记，避免请求过程中的频繁点击
            oEl.attr(sAttName, '1');
            that.renderMore(function () {
                // 取消点击标记位，可以进行下一次加载
                oEl.removeAttr(sAttName);
                // 没有数据隐藏加载更多按钮
                !that.listHasNext && oEl.html("没有数据")
            });
        });

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
                $.each(oResult.feeds, function (nIndex, vo) {
                    var date = moment().format('YYYY-MM-DD HH:mm:ss', vo.createdDate);
                    feed = JSON.parse(vo.data);
                    if (vo.type == 1) {
                        sHtml += that.tpl([

                            '<div class="feed-item folding feed-item-hook feed-item-2" feed-item-a="" data-type="a" id="feed-2" data-za-module="FeedItem" data-za-index="">',
                            '<meta itemprop="ZReactor" data-id="389034" data-meta="{&quot;source_type&quot;: &quot;promotion_answer&quot;, &quot;voteups&quot;: 4168, &quot;comments&quot;: 69, &quot;source&quot;: []}">',
                            '<div class="feed-item-inner">',
                            '<div class="avatar">',
                            '<a title="' + feed.userName + '" data-tip="p$t$amuro1230" class="zm-item-link-avatar" target="_blank" href="/user/' + feed.userId + '">',
                            '<img src="' + feed.userHead + '" class="zm-item-img-avatar">',
                            '</a>',
                            '</div>',
                            '<div class="feed-main">',
                            '<div class="feed-content" data-za-module="AnswerItem">',
                            '<meta itemprop="answer-id" content="389034">',
                            '<meta itemprop="answer-url-token" content="13174385">',

                            '<div class="expandable entry-body">',
                            '<div class="zm-item-answer-author-info">',
                            '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/user/' + feed.userId + '">' + feed.userName + '</a>',
                            '您评论了该问题，' + date + '</div>',

                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                            '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/question/' + feed.questionId + '">' + feed.questionTitle + '</a>',
                            '<p class="zm-profile-item-text">' + feed.comment + '</p>',
                            '</div>',
                            '</div>',
                            '</div>',

                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',


                        ].join(''), vo);
                    } else if (vo.type == 6) {
                        sHtml += that.tpl([

                            '<div class="feed-item folding feed-item-hook feed-item-2" feed-item-a="" data-type="a" id="feed-2" data-za-module="FeedItem" data-za-index="">',
                            '<meta itemprop="ZReactor" data-id="389034" data-meta="{&quot;source_type&quot;: &quot;promotion_answer&quot;, &quot;feedteups&quot;: 4168, &quot;comments&quot;: 69, &quot;source&quot;: []}">',
                            '<div class="feed-item-inner">',
                            '<div class="avatar">',
                            '<a title="' + feed.userName + '" data-tip="p$t$amuro1230" class="zm-item-link-avatar" target="_blank" href="/user/' + feed.userId + '">',
                            '<img src="' + feed.userHead + '" class="zm-item-img-avatar">',
                            '</a>',
                            '</div>',
                            '<div class="feed-main">',
                            '<div class="feed-content" data-za-module="AnswerItem">',
                            '<meta itemprop="answer-id" content="389034">',
                            '<meta itemprop="answer-url-token" content="13174385">',

                            '<div class="expandable entry-body">',

                            '<div class="zm-item-answer-author-info">',
                            '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/user/' + feed.userId + '">' + feed.userName + '</a>',
                            '您发表了提问 ，' + date + '</div>',

                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                            '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/question/' + feed.questionId + '">' + feed.questionTitle + '</a>', '</div>',
                            '</div>',
                            '</div>',

                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',


                        ].join(''), vo);
                    } else if (vo.type == 4&&feed.entityType==1) {
                        sHtml += that.tpl([

                            '<div class="feed-item folding feed-item-hook feed-item-2" feed-item-a="" data-type="a" id="feed-2" data-za-module="FeedItem" data-za-index="">',
                            '<meta itemprop="ZReactor" data-id="389034" data-meta="{&quot;source_type&quot;: &quot;promotion_answer&quot;, &quot;feedteups&quot;: 4168, &quot;comments&quot;: 69, &quot;source&quot;: []}">',
                            '<div class="feed-item-inner">',
                            '<div class="avatar">',
                            '<a title="' + feed.userName + '" data-tip="p$t$amuro1230" class="zm-item-link-avatar" target="_blank" href="/user/' + feed.userId + '">',
                            '<img src="' + feed.userHead + '" class="zm-item-img-avatar">',
                            '</a>',
                            '</div>',
                            '<div class="feed-main">',
                            '<div class="feed-content" data-za-module="AnswerItem">',
                            '<meta itemprop="answer-id" content="389034">',
                            '<meta itemprop="answer-url-token" content="13174385">',

                            '<div class="expandable entry-body">',

                            '<div class="zm-item-answer-author-info">',
                            '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/user/' + feed.userId + '">' + feed.userName + '</a>',
                            '您关注了问题 ，' + date + '</div>',

                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                            '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/question/' + feed.questionId + '">' + feed.questionTitle + '</a>', '</div>',
                            '</div>',
                            '</div>',

                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',


                        ].join(''), vo);
                    } else if (vo.type == 4&&feed.entityType==3) {
                        sHtml += that.tpl([

                            '<div class="feed-item folding feed-item-hook feed-item-2" feed-item-a="" data-type="a" id="feed-2" data-za-module="FeedItem" data-za-index="">',
                            '<meta itemprop="ZReactor" data-id="389034" data-meta="{&quot;source_type&quot;: &quot;promotion_answer&quot;, &quot;feedteups&quot;: 4168, &quot;comments&quot;: 69, &quot;source&quot;: []}">',
                            '<div class="feed-item-inner">',
                            '<div class="avatar">',
                            '<a title="' + feed.userName + '" data-tip="p$t$amuro1230" class="zm-item-link-avatar" target="_blank" href="/user/' + feed.userId + '">',
                            '<img src="' + feed.userHead + '" class="zm-item-img-avatar">',
                            '</a>',
                            '</div>',
                            '<div class="feed-main">',
                            '<div class="feed-content" data-za-module="AnswerItem">',
                            '<meta itemprop="answer-id" content="389034">',
                            '<meta itemprop="answer-url-token" content="13174385">',

                            '<div class="expandable entry-body">',

                            '<div class="zm-item-answer-author-info">',
                            '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/user/' + feed.userId + '">' + feed.userName + '</a>',
                            '您关注了用户 ，' + date + '</div>',

                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                            '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/user/' + vo.typeId + '">' + feed.entityUserName + '</a>', '</div>',
                            '</div>',
                            '</div>',

                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',


                        ].join(''), vo);
                    } else if (vo.type == 0) {
                        sHtml += that.tpl([

                            '<div class="feed-item folding feed-item-hook feed-item-2" feed-item-a="" data-type="a" id="feed-2" data-za-module="FeedItem" data-za-index="">',
                            '<meta itemprop="ZReactor" data-id="389034" data-meta="{&quot;source_type&quot;: &quot;promotion_answer&quot;, &quot;feedteups&quot;: 4168, &quot;comments&quot;: 69, &quot;source&quot;: []}">',
                            '<div class="feed-item-inner">',
                            '<div class="avatar">',
                            '<a title="' + feed.userName + '" data-tip="p$t$amuro1230" class="zm-item-link-avatar" target="_blank" href="/user/' + feed.userId + '">',
                            '<img src="' + feed.userHead + '" class="zm-item-img-avatar">',
                            '</a>',
                            '</div>',
                            '<div class="feed-main">',
                            '<div class="feed-content" data-za-module="AnswerItem">',
                            '<meta itemprop="answer-id" content="389034">',
                            '<meta itemprop="answer-url-token" content="13174385">',

                            '<div class="expandable entry-body">',

                            '<div class="zm-item-answer-author-info">',
                            '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/user/' + feed.userId + '">' + feed.userName + '</a>',
                            '您赞了该评论 ，' + date + '</div>',

                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                            '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/question/' + feed.questionId + '">' + feed.questionTitle + '</a>',
                            '<p class="zm-profile-item-text">' + feed.comment + '</p>',
                            '</div>',
                            '</div>',
                            '</div>',

                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',


                        ].join(''), vo);
                    }
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
        var sUrl = '/loadMoreProfileInfo/'+ oConf.offset + '/' + oConf.limit;
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