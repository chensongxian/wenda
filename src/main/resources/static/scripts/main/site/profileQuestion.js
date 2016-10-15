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
        that.listEl = $('#zh-profile-ask-inner-list');
        // 初始化数据
        that.offset = 0;
        that.limit = 10;
        that.listHasNext = true;

        that.renderMore(function () {
            // 没有数据隐藏加载更多按钮
            !that.listHasNext&& $('.zu-button-more').html("没有数据");;
        });

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

    }

    function fRenderMore(fCb) {
        var that = this;
        // 没有更多数据，不处理
        if (!that.listHasNext) {
            return;
        }
        that.requestData({
            userId:$('#zh-profile-ask-inner-list #userId').val(),
            offset: that.offset,
            limit: that.limit,
            call: function (oResult) {
                // 是否有更多数据
                that.listHasNext = oResult.hasNext;
                // alert(that.listHasNext);
                // 更新当前页面
                that.offset+=that.limit;
                // 渲染数据
                var sHtml = '';
                $.each(oResult.questionInfos, function (nIndex, questionInfo) {
                    sHtml += that.tpl([
                        '<div class="zm-profile-section-item zg-clear">',
                        '<span class="zm-profile-vote-count">',
                        '<div class="zm-profile-vote-num">'+questionInfo.followCount+'</div>',
                        '<div class="zm-profile-vote-type">关注</div>',
                        '</span>',
                        '<div class="zm-profile-section-main">',
                        '<h2 class="zm-profile-question">',
                        '<a class="question_link" href="/question/'+questionInfo.question.id+'" target="_blank" data-id="11072287">'
                        +questionInfo.question.title+
                        '</a>',
                        '</h2>',
                        '<div class="meta zg-gray">',
                        '<a data-follow="q:link" class="follow-link zg-unfollow meta-item" href="javascript:;" id="sfb-11072287">','<i class="z-icon-follow">','</i>取消关注</a>',
                        '<span class="zg-bull">'+',•'+'</span>'
                        +questionInfo.question.commentCount+'个回答'+
                        '</div>',
                        '</div>',
                        '</div>',

                    ].join(''), questionInfo);


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
        var sUrl = '/user/'+oConf.userId+'/question/' + oConf.offset + '/' + oConf.limit;
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