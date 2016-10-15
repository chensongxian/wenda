/**
var oPopupAdd = new PopupAdd({
    data: 初始数据
        toName: String, 姓名
        content: String, 内容
});
 */
(function (window) {
    var PopupMsg = Base.createClass('main.component.PopupMsg');
    var Popup = Base.getClass('main.component.Popup');
    var Component = Base.getClass('main.component.Component');
    var Util = Base.getClass('main.base.Util');

    Base.mix(PopupMsg, Component, {
        _tpl: [
            '<div class="zh-add-question-form" style="margin:0;">',
                '<div class="zg-section-big clearfix">',
                    '<div class="add-question-section-title">发给：</div>',
                    '<div class="zg-form-text-input add-question-title-form" style="position: relative;">',
                        '<input type="hidden" id="userId" value="0">',
                        '<input type="text" class="js-name zg-editor-input zu-seamless-input-origin-element" id="user-ajax" placeholder="请搜索用户" style="height:22px;min-height:auto;" data-provide="typeahead" data-items="4">',
                    '</div>',
                '</div>',
                '<div class="zg-section-big">',
                    '<div class="add-question-section-title">内容：</div>',
                    '<div id="zh-question-suggest-detail-container" class="zm-editable-status-editing">',
                        '<div class="zm-editable-editor-wrap no-toolbar">',
                            '<div class="zm-editable-editor-outer">',
                                '<div class="zm-editable-editor-field-wrap">',
                                    '<textarea class="js-content zm-editable-editor-field-element editable" placeholder="私信内容" style="font-style:italic;width:98%;"></textarea>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>',
            '</div>'].join(''),
        listeners: [{
            name: 'render',
            type: 'custom',
            handler: function () {
                var that = this;
                var oConf = that.rawConfig;
                var oEl = that.getEl();
                $("#user-ajax").typeahead(
                    {
                        source:function (query,process) {
                            console.log(query);
                            return $.ajax({
                                url: '/getUser/' + query,
                                type: 'get',
                                dataType: 'json',
                                success: function (result) {
                                    // 这里的数据解析根据后台传入格式的不同而不同
                                    //
                                    var resultList=[];
                                    $.each(result.vos, function (index, item) {
                                        var aItem = {id: item.id, name: item.name};
                                        resultList[index++]=JSON.stringify(aItem);
                                    });
                                    // var resultList=result.data.map(function (item) {
                                    //     var aItem = {id: item.topicId, name: item.topic};
                                    //     return JSON.stringify(aItem);
                                    // });
                                    if(resultList.length==0){
                                        $('#userId').val(0);
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
                            $('#userId').attr('value', item.id);
                            console.log("设置:"+$('#userId').val());
                            return item.name;
                        }
                    }
                );
                that.nameIpt = oEl.find('#userId');
                that.contentIpt = oEl.find('.js-content');

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
        var oAdd = new PopupMsg(oConf);
        var bSubmit = false;
        var oPopup = new Popup({
            width: 540,
            title: '发送私信',
            okTxt: '发送',
            content: oAdd.html(),
            ok: function () {
                var that = this;
                var oData = oAdd.val();
                alert(oData.toName);
                if (oData.toName==0) {
                    that.error('请填写姓名');
                    return true;
                }
                if (!oData.content) {
                    that.error('请填写私信内容');
                    return true;
                }
                // 避免重复提交
                if (bSubmit) {
                    return true;
                }
                bSubmit = true;
                // 提交内容
                $.ajax({
                    url: '/msg/addMessage',
                    type: 'post',
                    data: oData,
                    dataType: 'json'
                }).done(function (oResult) {
                    // 未登陆，跳转到登陆页面
                    if (oResult.code === 999) {
                        window.location.href = '/reglogin?next=' + window.encodeURIComponent(window.location.href);
                    } else if (oResult.code !== 0) {
                        that.error(oResult.msg || '出现错误，请重试');
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
        PopupMsg.superClass.initialize.apply(that, arguments);
    }

    function fVal(oData) {
        var that = this;
        if (arguments.length === 0) {
            return {
                toName: $.trim(that.nameIpt.val()),
                content: $.trim(that.contentIpt.val())
            };
        } else {
            oData = oData || {};
            that.nameIpt.val($.tirm(oData.toName));
            that.contentIpt.val($.trim(oData.content));
        }
    }

})(window);