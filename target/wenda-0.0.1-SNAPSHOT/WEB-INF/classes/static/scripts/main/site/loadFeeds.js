/**
 * Created by csx on 2016/9/23.
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
        that.offset = 10;
        that.limit = 10;
        that.listHasNext = true;

        // that.renderMore(function () {
        //     // 没有数据隐藏加载更多按钮
        //     !that.listHasNext&& $('.zu-button-more').html("没有数据");;
        // });

        // 绑定事件
        $('.zu-button-more').on('click', function (oEvent) {
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


        that.followeeOffset = 0;
        that.followeeHasNext = true;
        that.listFollowee = $('#js-followee-feed-list');

        that.agreeOffset = 0;
        that.agreeHasNext = true;
        that.listAgree = $('#js-agree-feed-list');
        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            // 获取已激活的标签页的名称
            var activeTab = $(e.target);

            if (activeTab.attr("id") == 'message') {

            } else if (activeTab.attr("id") == 'followee') {
                that.followeeHasNext = true;
                that.followeeOffset = 0;
                that.listFollowee.empty();
                showFollowee(that.followeeOffset, that.limit);
            } else if (activeTab.attr("id") == 'agree') {
                that.agreeHasNext = true;
                that.agreeOffset = 0;
                that.listAgree.empty();
                showAgree(that.agreeOffset,that.limit);
            }

        });

        $('#load-more-followee').click(function () {
            that.followeeOffset += that.limit;
            showFollowee(that.followeeOffset, that.limit)
        });

        $('#load-more-agree').click(function () {
            that.agreeOffset += that.limit;
            showAgree(that.agreeOffset, that.limit)
        });

        function showFollowee(offset, limit) {
            if (!that.followeeHasNext) {
                return;
            }
            $.ajax({
                type: "Get",
                url: "/loadMoreFollowee/" + offset + "/" + limit,
                dataType: 'json',
                success: function (oResult) {
                    // 是否有更多数据
                    that.followeeHasNext = oResult.hasNext;
                    // 更新当前页面
                    // 渲染数据
                    var sHtml = '';
                    $.each(oResult.feeds, function (nIndex, vo) {
                        var date = moment().format('YYYY-MM-DD HH:mm:ss', vo.createdDate);
                        feed = JSON.parse(vo.data);
                        if (feed.entityType == 1) {
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
                                '关注了您的问题 ，' + date + '</div>',

                                '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                                '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/question/' + feed.questionId + '">' + feed.questionTitle + '</a>',
                                '</div>',
                                '</div>',
                                '</div>',

                                '</div>',
                                '</div>',
                                '</div>',
                                '</div>',


                            ].join(''), vo);
                        } else if (feed.entityType == 3) {
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
                                '关注了您 ，' + date + '</div>',

                                '</div>',

                                '</div>',
                                '</div>',
                                '</div>',
                                '</div>',


                            ].join(''), vo);
                        }

                    });
                    sHtml && that.listFollowee.append(sHtml);
                },

                error: function () {
                    alert("发布失败");
                }
            });
        }


        function showAgree(offset, limit) {
            if (!that.agreeHasNext) {
                return;
            }
            $.ajax({
                type: "Get",
                url: "/loadMoreAgree/" + offset + "/" + limit,
                dataType: 'json',
                success: function (oResult) {
                    // 是否有更多数据
                    that.agreeHasNext = oResult.hasNext;
                    // 更新当前页面
                    // 渲染数据
                    var sHtml = '';
                    $.each(oResult.feeds, function (nIndex, vo) {
                        var date = moment().format('YYYY-MM-DD HH:mm:ss', vo.createdDate);
                        feed = JSON.parse(vo.data);

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
                            '赞了您的评论 ，' + date + '</div>',

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

                    });
                    sHtml && that.listAgree.append(sHtml);
                },

                error: function () {
                    alert("发布失败");
                }
            });
        }


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
                that.offset += that.limit;
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
                            '评论了该问题' + date + '</div>',

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
                            '发表了提问 ，' + date + '</div>',

                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                            '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/question/' + feed.questionId + '">' + feed.questionTitle + '</a>', '</div>',
                            '</div>',
                            '</div>',

                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',


                        ].join(''), vo);
                    } else if (vo.type == 4) {
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
                            '关注了问题 ，' + date + '</div>',

                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="123114" data-action="/answer/content" data-author-name="李淼" data-entry-url="/question/19857995/answer/13174385">',
                            '<div class="zh-summary summary clearfix">', '<a class="author-link" data-tip="p$b$amuro1230" target="_blank" href="/question/' + feed.questionId + '">' + feed.questionTitle + '</a>', '</div>',
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
                            '赞了该评论 ，' + date + '</div>',

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
        var sUrl = '/loadMoreFeeds/' + oConf.offset + '/' + oConf.limit;
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