(function (window, undefined) {
    var PopupAdd = Base.getClass('main.component.PopupAdd');
    var PopupMsg = Base.getClass('main.component.PopupMsg');
    var PopupImage = Base.getClass('main.component.PopupImage');

    Base.ready({
        initialize: fInitialize,
        binds: {
            'click #zu-top-add-question': fClickAdd,
            'click #zh-top-nav-count-wrap': fClickMsg,
            'click .side-profile-pic': fClickImage,
        }
    });

    function fInitialize() {
        var that = this;
    }

    function fClickAdd() {
        var that = this;
        PopupAdd.show({
            ok: function () {
                window.location.replace("/");
            }
        });
    }

    function fClickImage() {
        var that = this;
        PopupImage.show({
            ok: function () {
                window.location.replace("/");
            }
        });
    }

    function fClickMsg() {
        var that = this;
        PopupMsg.show({
            ok: function () {
                window.location.replace("/msg/list");
            }
        });
    }

})(window);
$(function () {
        var href = location.href.split('/').pop();
        $("#zh-top-nav-home").css("background", "#3D444C");
        if (href == "topic") {
            $("#zh-top-nav-topic").css("background", "#31363D");
        } else if (href == "pullfeeds") {
            $("#zh-top-nav-message").css("background", "#31363D");
            setNewFeedRead();
        } else if (href == '') {
            $("#zh-top-nav-home").css("background", "#31363D");
        }

        setInterval(isNewFeed,10000);

        function isNewFeed() {
            $.ajax({
                    type: "POST",
                    url: "/hasNew",
                    dataType: 'json',
                    success: function (oResult) {
                        if (oResult.hasNew) {
                            $('#zh-top-nav-message #msgNum').addClass('msg-num').text(oResult.count);
                        }
                    },
                    error:function () {
                        console.log("读取是否有新消息错误");
                    }
                }
            );
        }

        function setNewFeedRead() {
            $.ajax({
                    type: "POST",
                    url: "/setNewRead",
                    dataType: 'json',
                    success:function (oResult) {
                        if(oResult.code==0){
                            $('#zh-top-nav-message #msg-num').remove("msg num").text('');
                        }
                    },
                    error:function () {
                      console.log("设置已读错误")
                    }
                }
            );
        }


    }
)