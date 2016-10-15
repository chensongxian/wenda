/**
 var oPopupAdd = new PopupAdd({
    data: 初始数据
        toName: String, 姓名
        content: String, 内容
});
 */
(function (window) {
    var PopupImage = Base.createClass('main.component.PopupImage');
    var Popup = Base.getClass('main.component.Popup');
    var Component = Base.getClass('main.component.Component');
    var Util = Base.getClass('main.base.Util');

    var cp;
    Base.mix(PopupImage, Component, {
            _tpl: [
                '<div class="container">',
                '<div class="imageBox">',
                '<div class="thumbBox">', '</div>',
                '<div class="spinner" style="display: none">' + 'Loading...' + '</div>',
                '</div>',
                '<div class="action">',

                '<div class="new-contentarea tc">', '<a href="javascript:void(0)" class=" btn btn-primary upload-img">',
                '<label for="upload-file">' + '上传图像' + '</label>',
                '</a>',
                '<input type="file" class="" name="upload-file" id="upload-file" />',
                '</div>',
                '<input type="button" id="btnZoomIn" class="Btnsty_peyton" value="+"  >',
                '<input type="button" id="btnZoomOut" class="Btnsty_peyton" value="-" >',
                '</div>',
                '<div class="cropped">', '</div>',
                '</div>',
            ].join(''),
            listeners: [{
                name: 'render',
                type: 'custom',
                handler: function () {
                    var that = this;
                    var oConf = that.rawConfig;
                    var oEl = that.getEl();


                    var options =
                    {
                        thumbBox: '.thumbBox',
                        spinner: '.spinner',
                        imgSrc: '../images/avatar.png'
                    };
                    var cropper = $('.imageBox').cropbox(options);
                    cp=cropper
                    $('#upload-file').on('change', function () {
                        var that=this;
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            options.imgSrc = e.target.result;
                            cropper = $('.imageBox').cropbox(options);
                            cp=cropper;
                        }
                        reader.readAsDataURL(that.files.item(0));
                        // alert(that.files[0].name);
                        that.files = [];
                    })



                    $('#btnZoomIn').on('click', function () {
                        cropper.zoomIn();
                    })
                    $('#btnZoomOut').on('click', function () {
                        cropper.zoomOut();
                    })
                    // that.codeIpt = oEl.find('.js-code');
                    // 还原值
                    oConf.data && that.val(oConf.data);
                }
            }],
            show: fStaticShow
        },
        {
            initialize: fInitialize,
            val: fVal
        }
    )
    ;

    function fStaticShow(oConf) {
        var that = this;
        var oAdd = new PopupImage(oConf);
        var bSubmit = false;
        var oPopup = new Popup({
            width: 362,
            height: 400,
            title: '修改图片',
            okTxt: '确定',
            content: oAdd.html(),
            ok: function () {
                var that = this;
                var oData = oAdd.val();
                // alert(oData);
                // if (!oData.toCode) {
                //     that.error('请填写验证码');
                //     return true;
                // }


                var img=cp.getDataURL();
                // alert(img);
                // 避免重复提交
                if (bSubmit) {
                    return true;
                }
                bSubmit = true;

                $.ajax({
                    url: '/setHeadImg',
                    type: 'post',
                    data: {img:img},
                    dataType: 'json'
                }).done(function (oResult) {
                        if(oResult.code==0){
                            $('#avatarImage').attr("src",oResult.msg);
                            oConf.ok && oConf.ok.call(that);
                            oAdd.emit('ok');
                        }else if(oResult==1){
                            that.error(oResult.msg || '出现错误，请重试');
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
        PopupImage.superClass.initialize.apply(that, arguments);
    }

    function fVal(oData) {
        var that = this;
        if (arguments.length === 0) {
            return {
                // toCode: $.trim(that.codeIpt.val()),
            };
        } else {
            oData = oData || {};
            // that.codeIpt.val($.tirm(oData.toCode));
        }
    }

})(window);