/**
 * Created by csx on 2016/9/17.
 */
$(function () {
    var oExports = {
        initialize: fInitialize,
        // 渲染更多数据
        renderMore: fRenderMore,
        // 请求数据
        requestData: fRequestData,
        // 简单的模板替换
        tpl: fTpl,
        submit: fSubmit
    };
    // 初始化页面脚本
    oExports.initialize();

    function fInitialize() {
        var that = this;
        // 常用元素
        that.listEl = $('#zh-question-answer-wrap');
        that.questionId=$('#questionId').val();
        // 初始化数据
        that.offset = 0;
        that.limit = 10;
        that.listHasNext= true;

        that.renderMore(function () {
            // 没有数据隐藏加载更多按钮
            !that.listHasNext&& $('#load-more-comment').html("没有数据");
        });


        //summernote富文本编辑器初始化
        var placeholder = $('#content').attr("placeholder") || '';
        $('#content').summernote(
            {
                lang: 'zh-CN',
                placeholder: placeholder,
                minHeight: 200,
                dialogsFade: true,//
                dialogsInBody: true,//
                disableDragAndDrop: false,
                hint: {
                    // mentions:['jayden', 'sam', 'alvin', 'david'],
                    match: /\B@(\w*)$/,
                    search: function (keyword, callback) {
                        $.ajax({
                            type: "Get",
                            url: "/getUser/"+keyword,
                            dataType: 'json',
                        }).then(function (data) {
                            // var items=[];
                            // $(data.vos).each(function (index,item) {
                            //     items[index]=item.name;
                            // });
                            callback(data.vos);
                        });
                    },
                    content: function (item) {
                        console.log(item);
                        var href='/user/'+item.id;

                        return $('<a>').attr('href',href).css('text-decoration','none').text('@'+item.name)[0];
                    },
                    template: function(item) {
                        var href='/user/'+item.id;
                        return '@'+'<a href="'+href+'" style="text-decoration: none">' + item.name+ '</a>';
                    }
                },
                callbacks: {
                    onImageUpload: function (files) { //the onImageUpload API
                        img = sendFile(files);
                    }
                }
            }
        );



        $('#send').on("click", function (oEvent) {
            var questionId = $('#questionId').val();
            var content = $('#content').summernote('code');
            $.ajax({
                data: {questionId: questionId, content: content},
                type: "POST",
                url: "/addComment",
                dataType: 'json',
                success: function (message) {
                    // console.log(message.vo.comment);
                    if (message.code == 0) {
                        var vo = message.vo;
                        // console.log(vo.comment.content);
                        var sHtml = '';
                        var date = moment().format('YYYY-MM-DD HH:mm:ss', vo.comment.createdDate);
                        sHtml += that.tpl([
                            '<div class="zm-item-answer  zm-item-expanded js-comment">',
                            '<link itemprop="url" href="">',
                            '<meta itemprop="answer-id" content="22162611">',
                            '<meta itemprop="answer-url-token" content="66862039">',
                            '<a class="zg-anchor-hidden" name="answer-22162611">', '</a>',

                            '<div class="zm-votebar goog-scrollfloater js-vote" data-id="' + vo.comment.id + '">',
                            '<a class="zm-item-vote-count js-expand js-vote-count" href="javascript:;" data-bind-votecount="">',
                            '<span class="count js-voteCount">' + 0 + '</span>',
                            '<span class="label sr-only">' + '赞同' + '</span>',
                            '</a>',


                            '</div>',
                            '<div class="answer-head">',
                            '<div class="zm-item-answer-author-info">',
                            '<a class="zm-item-link-avatar avatar-link" href="" target="_blank" data-tip="p$t$yingxiaodao">',
                            '<img src="' + vo.user.headUrl + '" class="zm-list-avatar avatar">', '</a>',
                            '<a class="author-link" data-tip="p$t$yingxiaodao" target="_blank" href="">' + vo.user.name + '</a>',
                            '</div>',
                            '<div class="zm-item-vote-info">',
                            '<span class="voters text">',
                            '<a href="" class="more text">', '<span class="js-voteCount">' + 0 + '</span>' + '&nbsp;人赞同' + '</a>',
                            '</span>',
                            '</div>',
                            '</div>',
                            '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="6727688" data-action="/answer/content" data-author-name="营销岛" data-entry-url="/question/36301524/answer/66862039">',
                            '<div class="zm-editable-content clearfix">'+vo.comment.content+'</div>',
                            '</div>',
                            '<a class="zg-anchor-hidden ac" name="22162611-comment">', '</a>',
                            '<div class="zm-item-meta answer-actions clearfix js-contentActions">',
                            '<div class="zm-meta-panel">',
                            '<a itemprop="url" class="answer-data-link meta-item" target="_blank" href="">' + '发布于' + date + '</a>',
                            '<a href="" name="addcomment" class="meta-item toggle-comment js-toggleCommentBox">',
                            '<i class="z-icon-comment">', '</i>' + '0条评论' + '</a>',

                            '<button class="item-collapse js-collapse" style="transition: none;">',
                            '<i class="z-icon-fold">', '</i>' + '收起' +
                            '</button>',
                            '</div>',
                            '</div>',
                            '</div>',
                        ].join(''), vo);
                        sHtml && that.listEl.append(sHtml);
                        $('#content').summernote('reset');
                    }
                },

                error: function () {
                    alert("发布失败");
                }
            });
        });


        // 绑定事件
        $('#load-more-comment').on('click', function (oEvent) {
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

//发送图片
    function sendFile(files) {
        $(files).each(function () {
            var file = this;
            data = new FormData();
            data.append("file", file);
            $.ajax({
                data: data,
                type: "POST",
                url: "/uploadImage/",
                dataType: 'json',
                cache: false,
                contentType: false,
                processData: false,
                success: function (message) {
                   if(message.code==0){
                       $('#content').summernote('insertImage',message.msg,'img');
                   }

                },

                error: function () {
                    alert("评论失败");
                }
            });
        });
    }

    function fSubmit() {
        var content = $("#content").summernote('code');
        // console.log("xxx");
    }

    function fRenderMore(fCb) {
        var that = this;
        // 没有更多数据，不处理
        if (!that.listHasNext) {
            $('#load-more-comment').html("没有数据");
            return;
        }
        that.requestData({
            questionId: that.questionId,
            offset: that.offset,
            limit: that.limit,
            call: function (oResult) {
                // 是否有更多数据
                that.listHasNext = oResult.hasNext;
                // 更新当前页面
                that.offset+=that.limit;
                // 渲染数据
                var sHtml = oResult.html;
                // console.log(sHtml);
                // $.each(oResult.comments, function (nIndex, vo) {
                //     var date = moment().format('YYYY-MM-DD HH:mm:ss', vo.comment.createdDate);
                //     console.log(vo.liked);
                //     sHtml += that.tpl([
                //         '<div class="zm-item-answer  zm-item-expanded js-comment">',
                //         '<link itemprop="url" href="">',
                //         '<meta itemprop="answer-id" content="22162611">',
                //         '<meta itemprop="answer-url-token" content="66862039">',
                //         '<a class="zg-anchor-hidden" name="answer-22162611">', '</a>',
                //
                //         '<div class="zm-votebar goog-scrollfloater js-vote" data-id="' + vo.comment.id + '">',
                //         '#if('+vo.liked+'> 0)',
                //             '#else',
                //             '<button class="up js-like" title="赞同">',
                //                 '#end',
                //                 '<i class="icon vote-arrow">','</i>',
                //                 '<span class="count js-voteCount">'+vo.likeCount+'</span>',
                //                 '<span class="label sr-only">赞同</span>',
                //             '</button>',
                //             '#if('+vo.liked+' < 0)',
                //             '<button class="down js-dislike pressed" title="反对，不会显示你的姓名">',
                //                 '#else',
                //                 '<button class="down js-dislike" title="反对，不会显示你的姓名">',
                //                 '#end',
                //                 '<i class="icon vote-arrow">','</i>',
                //                 '<span class="label sr-only">'+'反对，不会显示你的姓名'+'</span>',
                //                 '</button>',
                //         '</div>',
                //         '<div class="answer-head">',
                //         '<div class="zm-item-answer-author-info">',
                //         '<a class="zm-item-link-avatar avatar-link" href="" target="_blank" data-tip="p$t$yingxiaodao">',
                //         '<img src="' + vo.user.headUrl + '" class="zm-list-avatar avatar">', '</a>',
                //         '<a class="author-link" data-tip="p$t$yingxiaodao" target="_blank" href="">' + vo.user.name + '</a>',
                //         '</div>',
                //         '<div class="zm-item-vote-info">',
                //         '<span class="voters text">',
                //         '<a href="" class="more text">', '<span class="js-voteCount">' +vo.likeCount+ '</span>' + '&nbsp;人赞同' + '</a>',
                //         '</span>',
                //         '</div>',
                //         '</div>',
                //         '<div class="zm-item-rich-text expandable js-collapse-body" data-resourceid="6727688" data-action="/answer/content" data-author-name="营销岛" data-entry-url="/question/36301524/answer/66862039">',
                //         '<div class="zm-editable-content clearfix">'+vo.comment.content+'</div>',
                //         '</div>',
                //         '<a class="zg-anchor-hidden ac" name="22162611-comment">', '</a>',
                //         '<div class="zm-item-meta answer-actions clearfix js-contentActions">',
                //         '<div class="zm-meta-panel">',
                //         '<a itemprop="url" class="answer-data-link meta-item" target="_blank" href="">' + '发布于' + date + '</a>',
                //         '<a href="" name="addcomment" class="meta-item toggle-comment js-toggleCommentBox">',
                //         '<i class="z-icon-comment">', '</i>' + '0条评论' + '</a>',
                //
                //         '<button class="item-collapse js-collapse" style="transition: none;">',
                //         '<i class="z-icon-fold">', '</i>' + '收起' +
                //         '</button>',
                //         '</div>',
                //         '</div>',
                //         '</div>',
                //     ].join(''),vo);
                // });
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
        var sUrl = '/question/' + oConf.questionId + '/' + oConf.offset + '/' + oConf.limit ;
        // console.log(sUrl);
        $.ajax({url: sUrl, dataType: 'json'}).done(oConf.call).fail(oConf.error).always(oConf.always);
    }

    function fTpl(sTpl, oData) {
        var that = this;
        sTpl = $.trim(sTpl);
        return sTpl.replace(/#{(.*?)}/g, function (sStr, sName) {
            return oData[sName] === undefined || oData[sName] === null ? '' : oData[sName];
        });
    }
})
;