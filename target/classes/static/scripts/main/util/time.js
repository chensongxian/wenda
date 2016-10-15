/**
 * Created by csx on 2016/9/11.
 */
var time=60;
function showtime() {
    console.log(time);
    $("#sendMail").innerHTML=time;
    time--;
    if(time==0){
        clearInterval(mytime);
        console.log("结束");
        $("#sendMail").innerHTML="倒计时";
        time=10;
    }
}
$(function () {
    $("#sendMail").bind("click",function () {
        mytime=setInterval(showtime,1000);
    })
})