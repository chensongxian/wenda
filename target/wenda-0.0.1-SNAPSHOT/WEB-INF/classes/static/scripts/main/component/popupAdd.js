/**
var oPopupAdd = new PopupAdd({
    data: 初始数据
    ok: Function, 发布成功后的回调
});
 */
(function (window) {
    var PopupAdd = Base.createClass('main.component.PopupAdd');
    var Popup = Base.getClass('main.component.Popup');
    var Component = Base.getClass('main.component.Component');
    var Util = Base.getClass('main.base.Util');

    Base.mix(PopupAdd, Component, {
        _tpl: [
            '<div class="zh-add-question-form">',
                '<div class="zg-section-big clearfix">',
                    '<div class="zg-form-text-input add-question-title-form" style="position: relative;">',
                        '<input type="text" class="js-title zg-editor-input zu-seamless-input-origin-element" placeholder="写下你的问题" style="height:22px;min-height:auto;"></textarea>',
                    '</div>',
                '</div>',
                '<div class="zg-section-big">',
                    '<div class="add-question-section-title">问题说明（可选）：</div>',
                        '<div class="js-content" id="content" name="content" placeholder="评论"></div>',
                    '</div>',
                '</div>',
                '<div class="zg-section-big clearfix">',
                    '<div class="zg-form-text-input add-question-topic-form" style="position: relative;">',
                        '<input type="hidden" id="topic_id" value="0">',
                        '<input type="text"  class="js-topic zg-editor-input zu-seamless-input-origin-element" id="autocomplete-ajax" placeholder="请搜索话题" style="height:22px;min-height:auto;" data-provide="typeahead" data-items="4"/>',
                    '</div>',
                    '<div id="selction-ajax">',
                    '</div>',
                '</div>',
            '</div>'].join(''),
        listeners: [{
            name: 'render',
            type: 'custom',
            handler: function () {
                // var subjects = ['PHP', 'MySQL', 'SQL', 'PostgreSQL', 'HTML', 'CSS', 'HTML5', 'CSS3', 'JSON'];
                // $('#autocomplete-ajax').typeahead({source: subjects});
                var placeholder = $('#content').attr("placeholder") || '';
                $('#content').summernote(
                    {
                        lang: 'zh-CN',
                        height: 110,
                        toolbar: [
                            // [groupName, [list of button]]
                            ['style', ['bold', 'italic', 'underline', 'clear']],
                            ['fontsize', ['fontsize']],
                            ['color', ['color']],
                            ['para', ['ul', 'ol', 'paragraph']],
                            ['picture', ['picture']],
                            ['video', ['video']]

                        ],
                        placeholder: placeholder,
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
                                alert("插图片失败");
                            }
                        });
                    });
                }

                $("#autocomplete-ajax").typeahead(
                    {
                        source:function (query,process) {
                            console.log(query);
                            return $.ajax({
                                url: '/getTopic/' + query + '/',
                                type: 'get',
                                dataType: 'json',
                                success: function (result) {
                                    // 这里的数据解析根据后台传入格式的不同而不同
                                    //
                                    var resultList=[];
                                    $.each(result.data, function (index, item) {
                                        var aItem = {id: item.topicId, name: item.topic};
                                        resultList[index++]=JSON.stringify(aItem);
                                    });
                                    // var resultList=result.data.map(function (item) {
                                    //     var aItem = {id: item.topicId, name: item.topic};
                                    //     return JSON.stringify(aItem);
                                    // });
                                    if(resultList.length==0){
                                        $('#topic_id').val(0);
                                    }
                                    return process(resultList);

                                }
                            });
                        },
                        highlighter: function (obj) {
                            var item = JSON.parse(obj);
                            var query = this.query.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g, '\\$&')
                            return item.name.replace(new RegExp('(' + query + ')', 'ig'), function ($1, match) {
                                        return '<strong>' + match + '</strong>'
                            })
                        },
                        // matcher: function (obj) {
                        //     var item = JSON.parse(obj);
                        //     console.log("matcher:"+item.name);
                        //     console.log("index:"+item.name.toLowerCase().indexOf(this.query.toLowerCase()));
                        //     return item.name.toLowerCase().indexOf(this.query.toLowerCase());
                        // },
                        updater: function (obj) {
                            var item = JSON.parse(obj);
                            $('#topic_id').attr('value', item.id);
                            console.log("设置:"+$('#topic_id').val());
                            return item.name;
                        }
                    }
                );
                var that = this;
                var oConf = that.rawConfig;
                var oEl = that.getEl();
                that.titleIpt = oEl.find('.js-title');
                that.contentIpt = oEl.find('.js-content');
                that.topicIdIpt = oEl.find('#topic_id');
                // 还原值
                oConf.data && that.val(oConf.data);
            }
        }],
        show: fStaticShow
    }, {
        initialize: fInitialize,
        val: fVal
    });

    function fStaticShow(oConf) {
        var that = this;
        var oAdd = new PopupAdd(oConf);
        var bSubmit = false;



        var oPopup = new Popup({
            width: 540,
            title: '提问',
            okTxt: '发布',
            content: oAdd.html(),
            ok: function () {
                var that = this;
                var oData = oAdd.val();
                if (!oData.title) {
                    that.error('请填写标题');
                    return true;
                }

                oData.topicId=$('#topic_id').val();
                if(oData.topicId==0){
                    that.error('请用搜索主题');
                    return true;
                }
                // 避免重复提交
                if (bSubmit) {
                    return true;
                }
                bSubmit = true;
                console.log(oData);
                // 提交内容
                $.ajax({
                    url: '/question/add',
                    type: 'post',
                    data: oData,
                    dataType: 'json'
                }).done(function (oResult) {
                    // 未登陆，跳转到登陆页面
                    if (oResult.code === 999) {
                        window.location.href = '/reglogin?next=' + window.encodeURIComponent(window.location.href);
                    } else {
                        oConf.ok && oConf.ok.call(that);
                        oAdd.emit('ok');
                    }
                }).fail(function () {
                    alert('出现错误，请重试');
                }).always(function () {
                    bSubmit = false;
                });
                // 先不关闭
                return true;
            },
            listeners: {
                destroy: function () {
                    oAdd.destroy();
                }
            }
        });
        oAdd._popup = oPopup;
        Component.setEvents();
    }

    function fInitialize(oConf) {
        var that = this;
        delete oConf.renderTo;


        PopupAdd.superClass.initialize.apply(that, arguments);
    }

    function fVal(oData) {
        var that = this;
        if (arguments.length === 0) {
            return {
                title: $.trim(that.titleIpt.val()),
                content: $.trim(that.contentIpt.summernote('code')),
                topicId:$.trim(that.topicIdIpt.val())
            };
        } else {
            oData = oData || {};
            that.titleIpt.val($.tirm(oData.title));
            that.contentIpt.val($.trim(oData.content));
            that.topicIdIpt.val($.trim(oData.topicId));
        }
    }

})(window);