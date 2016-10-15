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
        that.listEl = $('#searchContentList');
        // 初始化数据
        that.offset = 10;
        that.limit = 10;
        that.listHasNext = true;


        that.searchUserOffset = 0;
        that.searchUserHasNext = true;
        that.listSearchUser = $('#js-user-list');


        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            // 获取已激活的标签页的名称
            var activeTab = $(e.target);
            // var that=this;
            if (activeTab.attr("id") == 'content_tab') {
                // alert("xxx");
                $('#searchType').val("content");
                // alert(that.listHasNext);
                that.listHasNext = true;
                that.offset = 0;
                that.listEl.empty();
                that.renderMore(function () {
                    // 没有数据隐藏加载更多按钮
                    !that.listHasNext && $('#search-load-content').html("没有数据");
                });
            } else if (activeTab.attr("id") == 'user_tab') {
                $('#searchType').val("user");
                that.searchUserHasNext = true;
                that.searchUserOffset = 0;
                that.listSearchUser.empty();
                searchUser(that.searchUserOffset, that.limit);
            }
        });


        $('#search-load-user').click(function () {
            that.searchUserOffset += that.limit;
            searchUser(that.searchUserOffset, that.limit)
        });

        function searchUser(offset, limit) {
            if (!that.searchUserHasNext) {
                !that.searchUserHasNext && $('#search-load-user').html("没有数据");
                return;
            }
            var keyword = $('#keyword').val();
            // alert(keyword);
            $.ajax({
                type: "Get",
                url: "/searchUser/" + offset + "/" + limit + "/" +keyword,
                dataType: 'json',
                success: function (oResult) {
                    // 是否有更多数据
                    that.searchUserHasNext = oResult.hasNext;
                    // 更新当前页面
                    // 渲染数据
                    var sHtml = '';
                    $.each(oResult.userList, function (nIndex, vo) {
                        sHtml += that.tpl([
                            '<div class="zm-profile-card zm-profile-section-item zg-clear no-hovercard">',
                        ].join(''), vo);
                        // alert(sHtml);
                        if (vo.followed) {
                            sHtml += that.tpl([
                                '<div class="zg-right">',
                                '<button class="zg-btn zg-btn-unfollow zm-rich-follow-btn small nth-0 js-follow-user" data-status="1" data-id="' + vo.user.id + '">' + '取消关注' + '</button>',
                                '</div>',
                            ].join(''), vo);
                        } else {
                            sHtml += that.tpl([
                                '<div class="zg-right">',
                                '<button class="zg-btn zg-btn-follow zm-rich-follow-btn small nth-0 js-follow-user">' + '关注' + '</button>',
                                '</div>',
                            ].join(''), vo);
                        }

                        sHtml += that.tpl([

                            '<a title="Barty" class="zm-item-link-avatar" href="/user/' + vo.user.id + '">',
                            '<img src="' + vo.user.headUrl + '" class="zm-item-img-avatar">',
                            '</a>',
                            '<div class="zm-list-content-medium">',
                            '<h2 class="zm-list-content-title">', '<a data-tip="p$t$buaabarty" href="/user/' + vo.user.id + '" class="zg-link" title="Barty">' + vo.user.name + '</a>', '</h2>',

                            '<div class="details zg-gray">',
                            '<a target="_blank" href="/user/' + vo.user.id + '/followers" class="zg-link-gray-normal">' + vo.followerCount + '粉丝' + '</a>' + '/',
                            '<a target="_blank" href="/user/' + vo.user.id + '/followees" class="zg-link-gray-normal">' + vo.followeeCount + '关注' + '</a>' + '/',
                            '<a target="_blank" href="#" class="zg-link-gray-normal">' + vo.commentCount + '回答' + '</a>' + '/',
                            '<a target="_blank" href="#" class="zg-link-gray-normal">' + '548 赞同' + '</a>',
                            '</div>',
                            '</div>',
                            '</div>',
                        ].join(''), vo);

                    });
                    // alert(sHtml);
                    sHtml && that.listSearchUser.append(sHtml);
                },

                error: function () {
                    alert("发布失败");
                }
            });
        }

        // 绑定事件
        $('#search-load-content').on('click', function (oEvent) {
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
            // alert("没有数据");
            return;
        }
        that.requestData({
            keyword: $('#keyword').val(),
            offset: that.offset,
            limit: that.limit,
            call: function (oResult) {
                // alert("xx");
                // 是否有更多数据
                that.listHasNext = oResult.hasNext;
                // 更新当前页面
                that.offset += that.limit;
                // 渲染数据
                var sHtml = '';
                $.each(oResult.vos, function (nIndex, searchInfo) {
                    var date = moment().format('YYYY-MM-DD HH:mm:ss', searchInfo.question.createdDate);
                    if (searchInfo.type == 1) {
                        sHtml += that.tpl([
                            '<li class="item clearfix">',
                            '<div class="title">',
                            '<a target="_blank" href="/user/' + searchInfo.user.id + '" class="js-title-link">' + searchInfo.question.title + '</a>',
                            '</div>',
                            '<div class="content">',

                            '<ul class="answers">',
                            '<li class="answer-item clearfix">',
                            '<div class="entry answer">',
                            '<div class="entry-left hidden-phone">',
                            '<a class="zm-item-vote-count hidden-expanded js-expand js-vote-count" data-bind-votecount="">' + searchInfo.followCount + '</a>',
                            '</div>',
                            '<div class="entry-body">',
                            '<div class="entry-meta">',
                            '<strong class="author-line">', '<a class="author" href="/user/' + searchInfo.user.id + '">' + searchInfo.user.name +
                            '</a>' + '，' + date +
                            '</strong>',
                            '</div>',
                            '<div class="entry-content js-collapse-body">',
                            '<div class="summary hidden-expanded" style="">'
                            + searchInfo.question.content +
                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',
                            '</li>',
                            '</ul>',
                            '</div>',
                            '</li>',

                        ].join(''), searchInfo);

                    } else if (searchInfo.type == 2) {
                        sHtml += that.tpl([
                            '<li class="item clearfix">',
                            '<div class="title">',
                            '<a target="_blank" href="/question/' + searchInfo.question.id + '" class="js-title-link">' + searchInfo.question.title + '</a>',
                            '</div>',
                            '<div class="content">',

                            '<ul class="answers">',
                            '<li class="answer-item clearfix">',
                            '<div class="entry answer">',
                            '<div class="entry-left hidden-phone">',
                            '<a class="zm-item-vote-count hidden-expanded js-expand js-vote-count" data-bind-votecount="">' + searchInfo.likeCount + '</a>',
                            '</div>',
                            '<div class="entry-body">',
                            '<div class="entry-meta">',
                            '<strong class="author-line">', '<a class="author" href="/user/' + searchInfo.user.id + '">' + searchInfo.user.name +
                            '</a>' + '，' + date +
                            '</strong>',
                            '</div>',
                            '<div class="entry-content js-collapse-body">',
                            '<div class="summary hidden-expanded" style="">'
                            + searchInfo.comment.content +
                            '</div>',
                            '</div>',
                            '</div>',
                            '</div>',
                            '</li>',
                            '</ul>',
                            '</div>',
                            '</li>',

                        ].join(''), searchInfo);
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
        var sUrl = '/searchMore/' + oConf.offset + '/' + oConf.limit + '/' + oConf.keyword;
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