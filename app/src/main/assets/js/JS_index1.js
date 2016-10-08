





var a=document.getElementsByName('1_id');
var b=document.getElementsByName('2_bt');
var c=document.getElementsByName('3_zy');
var d=document.getElementsByName('4_dz');
var e=document.getElementsByName('5_pl');
var f=document.getElementsByName('6_rq');
var g=document.getElementsByName('7_lj');
var imgs=document.getElementsByName('img');
var table=document.getElementById('down');


jQuery(document).ready(function(){

    $.ajax({
        type : "get",
        async:false,
        url : "http://wap.sirui-photo.com/apireview.php?artnum=5&pid=219", //接口网址， 返回文章数：artnum (缺省默认5条)；官网产品ID：pid
        dataType : "jsonp",
        jsonp: "callback",
        async: false,
        success : function(jsondata){
            var jsonLength=0;
            for(var item in jsondata)
            {
                jsonLength++;
            }
            for(var i=0; i<jsonLength; i++)
            {

                $("#down tbody").append('<tr><td class="cell_1"> <img name="img" class="img"/></td><td><p name="1_id">文章ID</p><h3 name="2_bt">标题</h3><p name="3_zy">文章摘要</p><span name="4_dz">点赞数</span> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span name="5_pl">评论数</span> <div class="cell_right"> <span name="6_rq">发布日期</span>&nbsp;&nbsp;<span name="7_lj">官网链接</span> </div> </td> </tr>');
                //var imgs=document.getElementsByName('img');
                a[i].innerHTML=jsondata[i].id;
                b[i].innerHTML=jsondata[i].articletitle;
                c[i].innerHTML=jsondata[i].summary;
                d[i].innerHTML=jsondata[i].praisenum;
                e[i].innerHTML=jsondata[i].commentnum;
                f[i].innerHTML=jsondata[i].releasedate;
                g[i].innerHTML=jsondata[i].url;
                imgs[i].src="http://www.sirui-photo.com/uploadfile/"+jsondata[i].picurl;
                alert(jsondata[1].url);

                //imgs[i].src=jsondata[i].picurl;
                //alert(jsondata[1].lpicur);

                // console.log((jsondata[i].picurl));
                // imgs.src="url(http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/03/0F/ChMkJlfgkP-IUo1NAANjoJxYxQUAAWM6QI9V9IAA2O4787.jpg)";
                //$('img').attr('src','http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/03/0F/ChMkJlfgkP-IUo1NAANjoJxYxQUAAWM6QI9V9IAA2O4787.jpg');

                //返回数据参数说明：
                //jsondata[i].id 文章id
                //jsondata[i].lpicur 图片路径
                //jsondata[i].releasedate 发布日期(年月日)
                //jsondata[i].articletitle 文章标题
                //jsondata[i].summary 文章摘要
                //jsondata[i].url 文章官网链接
                //jsondata[i].commentnum 评论数
                //jsondata[i].praisenum 点赞数
            }
        },
        error:function(){
        }
    });
});

//*****************************************************************************************************************************************************************

