/**
 * Created by csx on 2016/9/20.
 */
$(function () {
        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            // 获取已激活的标签页的名称
            var activeTab = $(e.target).parent().addClass("selected");
            // 获取前一个激活的标签页的名称
            var previousTab = $(e.relatedTarget).parent().removeClass("selected");

        });

        $('.btn-info-edit').click(function () {
            $('#basicInfo').addClass("edit-box")
        });

        $('.btn-info-cancel').click(function () {
            $('#basicInfo').removeClass('edit-box');
        });
        //邮箱校验
        var emailTip = $('.new-email~.input-tip');
        $('.new-email').focus(function () {
            $(this).parent('.control-group').removeClass("error");
            $(this).parent('.control-group').removeClass("success");

            emailTip.css('display', 'none');
        });

        $('.new-email').blur(function () {
            var email = $(this).val();
            if (Base.getClass('main.base.Util').isEmail(email)) {
                $(this).parent('.control-group').addClass("success");
                emailTip.css('display', 'none');
            } else {
                emailTip.css('display', '');
                $(this).parent('.control-group').addClass("error");
                emailTip.text('请输入正确的邮箱');
            }
        });
        //密码校验
        var oldPwTip = $('.old-pw~.input-tip');
        var newPwTip = $('.new-pw~.input-tip');
        var newRepwTip = $('.new-repw~.input-tip');

        $('.old-pw').focus(function () {
            $(this).parent('.control-group').removeClass("error");

            oldPwTip.css('display', 'none');
        });

        $('.old-pw').blur(function () {
            var oldPw = $(this).val();
            var newPw = $('.new-pw').val();
            if (!isEmpty(oldPw) && !isEmpty(newPw) && oldPw == newPw) {
                newPwTip.parent('.control-group').addClass("error")
                $(this).parent('.control-group').addClass("error");

                oldPwTip.css('display', '');
                newPwTip.css('display', '');
                oldPwTip.text('新旧密码不能相同');
                newPwTip.text('新旧密码不能相同');
            } else {
                newPwTip.parent('.control-group').removeClass("error")
                $(this).parent('.control-group').removeClass("error");

                oldPwTip.css('display', 'none');
                newPwTip.css('display', 'none');
            }
        });

        $('.new-pw').focus(function () {
            $(this).parent('.control-group').removeClass("error");

            emailTip.css('display', 'none');
        });

        $('.new-pw').blur(function () {
            var oldPw = $('.old-pw').val();
            var newPw = $(this).val();
            var newRepw = $('.new-repw').val();
            if (!isEmpty(oldPw) && !isEmpty(newPw) && oldPw == newPw) {
                oldPwTip.parent('.control-group').addClass("error");
                $(this).parent('.control-group').addClass("error");

                oldPwTip.css('display', '');
                newPwTip.css('display', '');
                oldPwTip.text('新旧密码不能相同');
                newPwTip.text('新旧密码不能相同');
            } else if (!isEmpty(newPw) && !isEmpty(newRepw) && newPw != newRepw) {
                newRepwTip.parent('.control-group').addClass("error")
                $(this).parent('.control-group').addClass("error");

                newRepwTip.css('display', '');
                newPwTip.css('display', '');
                newRepwTip.text('密码不一致');
                newPwTip.text('密码不一致');
            } else {
                oldPwTip.parent('.control-group').removeClass("error")
                $(this).parent('.control-group').removeClass("error");

                newRepwTip.css('display', 'none');
                newPwTip.css('display', 'none');
            }
        });

        $('.new-repw').focus(function () {
            $(this).parent('.control-group').removeClass("error");

            newRepwTip.css('display', 'none');
        });

        $('.new-repw').blur(function () {
            var newPw = $('.new-pw').val();
            var newRepw = $(this).val();
            if (!isEmpty(newPw) && !isEmpty(newRepw) && newPw != newRepw) {
                newPwTip.parent('.control-group').addClass("error");
                $(this).parent('.control-group').addClass("error");

                newRepwTip.css('display', '');
                newPwTip.css('display', '');
                newRepwTip.text('密码不一致');
                newPwTip.text('密码不一致');
            } else {
                newPwTip.parent('.control-group').removeClass("error")
                $(this).parent('.control-group').removeClass("error");

                newRepwTip.css('display', 'none');
                newPwTip.css('display', 'none');
            }
        });

        function isEmpty(str) {
            var str = $.trim(str);
            if (str == '' || str == null || str == undefined) {
                return true;
            } else {
                return false;
            }
        }

        //更改邮件
        $('#emailSave').click(function () {
            var email = $(".new-email").val();
            if (isEmpty(email) || !Base.getClass('main.base.Util').isEmail(email)) {

                alert("请输入正确邮件");
                return;
            }

            window.location.href = '/toSendEmail/' + email + '/';


        })

        $('#pwSave').click(function () {
            var oldPw = $(".old-pw").val();
            var newPw = $(".new-pw").val();
            var newRepw = $(".new-repw").val();

            if (isEmpty(oldPw) || isEmpty(newPw) || oldPw == newPw) {
                alert("请输入正确的密码");
                return;
            }
            if (isEmpty(newRepw) || newPw != newRepw) {
                alert("两次密码不相同");
                return;
            }

            $.ajax({
                data: {oldPw: oldPw, newPw: newPw},
                type: "POST",
                url: "/updatePw",
                dataType: 'json',
                success: function (msg) {
                    if (msg.code == 1) {
                        oldPwTip.parent('.control-group').addClass("error");
                        oldPwTip.css('display', '');
                        oldPwTip.text(msg.msg);
                    } else if (msg.code == 0) {
                        window.location.href = "/success";
                    }
                },
                error: function () {
                    alert("发生错误")
                }
            });
        });


        var nickTip=$("#nickname~.input-tip");
        $("#nickname").focus(function () {
            $(this).parent('.control-group').removeClass("error");

            nickTip.css('display', 'none');
        });

        $("#nickname").blur(function () {
            var name=$("#nickname").val();
            $.ajax({
                data: {name:name},
                type: "POST",
                url: "/haveName",
                dataType: 'json',
                success: function (msg) {
                    if (msg.code == 1) {
                        nickTip.parent('.control-group').addClass("error");
                        nickTip.css('display', '');
                        nickTip.text(msg.msg);
                    }
                },
                error: function () {
                    alert("发生错误")
                }
            });

        });

        $(".sl-sex-box .gender-edit").click(function () {
            $(this).addClass("selected").siblings().removeClass("selected");
        });

        $('.btn-info-save').click(function () {
            var name=$("#nickname").val();
            var sex=$(".sl-sex-box .selected").attr("data-sex");
            if(sex=='男'){
                sex=true;
            }else if(sex=='女'){
                sex=false;
            }

            var introduction=$('#introduction').val();
            var livePlace=$('#livePlace').val();
            $.ajax({
                data: {name:name,sex:sex,introduction:introduction,livePlace:livePlace},
                type: "POST",
                url: "/updateInfo",
                dataType: 'json',
                success: function (msg) {
                    if (msg.code == 1) {
                        nickTip.parent('.control-group').addClass("error");
                        nickTip.css('display', '');
                        nickTip.text(msg.msg);
                    } else if (msg.code == 0) {
                        $('#basicInfo').removeClass("edit-box");
                        $('#nicknamePart').text(name);
                        $('#nickname').text(name);
                        if(sex==true){
                            $('.sex-male').addClass('selected').siblings().removeClass('selected');
                            $('.gender-show-icon').removeClass('girl').addClass('boy');
                        }else if(sex==false){
                            $('.sex-female').addClass('selected').siblings().removeClass('selected');
                            $('.gender-show-icon').removeClass('boy').addClass('girl');
                        }
                        $('#introductionPart').text("introduction");
                        $('#introduction').text(introduction);
                        $('#livePlace').text(livePlace);
                        $('#livePlacePart').text(livePlace);
                    }
                },
                error: function () {
                    alert("发生错误")
                }
            });


        });
    });