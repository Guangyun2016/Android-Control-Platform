




$(function(){
    var counter = 0;
    // 每页展示4个
    var num = 2;
    var pageStart = 0,pageEnd = 0;

    // dropload
    $('.content').dropload({
        scrollArea : window,
        domUp : {
            domClass   : 'dropload-up',
            domRefresh : '<div class="dropload-refresh">↓下拉刷新-</div>',
            domUpdate  : '<div class="dropload-update">↑释放更新</div>',
            domLoad    : '<div class="dropload-load"><span class="loading"></span>刷新中...</div>'
        },
        domDown : {
            domClass   : 'dropload-down',
            domRefresh : '<div class="dropload-refresh">↑上拉加载更多</div>',
            domLoad    : '<div class="dropload-load"><span class="loading"></span>加载中...</div>',
            domNoData  : '<div class="dropload-noData">暂无数据</div>'
        },

        loadUpFn : function(me){

            //**************************************************************************************************************
            $.ajax({
                type : "get",
                async:false,
                url : "http://wap.sirui-photo.com/apireview.php?artnum=5&pid=219", //接口网址， 返回文章数：artnum (缺省默认5条)；官网产品ID：pid
                dataType : "jsonp",
                jsonp: "callback",
                async: false,
                success : function(jsondata){
                    var result = '';
                    var jsonLength=0;
                    for(var item in jsondata)
                    {
                        jsonLength++;
                    }
                    for(var i=0; i<jsonLength; i++)
                    {
                        result += '<table class="tab">'+
                            '<tr>'+
                            '<td class="td1">'+
                            '<img class="img" src="../img/k1.jpg" >'+
                            '</td>'+
                            '<td>'+
                            '<span class="number">'+jsondata[i].id+'</span>'+
                            '<span class="date">'+jsondata[i].releasedate+'</span>'+
                            '<p class="title">'+jsondata[i].articletitle+'</p>'+
                            '<p class="intro">'+jsondata[i].summary+'</p>'+
                            '<span class="praise">'+jsondata[i].praisenum+'</span>'+
                            '&nbsp;&nbsp;&nbsp;&nbsp;'+
                            '<span class="comment">'+jsondata[i].commentnum+'</span>'+
                            '<p class="hyperlink">'+jsondata[i].url+'</p>'+
                            '</td>'+
                            '</tr>'+
                            '</table>';
                        //var imgs=document.getElementsByName('img');



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
                    // 为了测试，延迟1秒加载
                    setTimeout(function(){
                        $('.lists').html(result);
                        // 每次数据加载完，必须重置
                        me.resetload();
                        // 重置索引值，重新拼接more.json数据
                        counter = 0;
                        // 解锁
                        me.unlock();
                        me.noData(false);
                    },1000);


                },
                error: function(xhr, type){
                    alert('Ajax error!');
                    // 即使加载出错，也得重置
                    me.resetload();
                }
            });
//**************************************************************************************************************


        },
        loadDownFn : function(me){


//**************************************************************************************************************
            $.ajax({
                type : "get",
                async:false,
                url : "http://wap.sirui-photo.com/apireview.php?artnum=5&pid=219", //接口网址， 返回文章数：artnum (缺省默认5条)；官网产品ID：pid
                dataType : "jsonp",
                jsonp: "callback",
                async: false,
                success : function(jsondata){
                    var result = '';
                    counter++;
                    pageEnd = num * counter;
                    pageStart = pageEnd - num;

                    var jsonLength=0;
                    for(var item in jsondata)
                    {
                        jsonLength++;
                    }
                    for(var i = pageStart; i < pageEnd; i++)
                    {

                        result += '<table class="tab">'+
                            '<tr>'+
                            '<td class="td1">'+
                            '<img class="img" src="../img/k1.jpg" >'+
                            '</td>'+
                            '<td>'+
                            '<span class="number">'+jsondata[i].id+'</span>'+
                            '<span class="date">'+jsondata[i].releasedate+'</span>'+
                            '<p class="title">'+jsondata[i].articletitle+'</p>'+
                            '<p class="intro">'+jsondata[i].summary+'</p>'+
                            '<span class="praise">'+jsondata[i].praisenum+'</span>'+
                            '&nbsp;&nbsp;&nbsp;&nbsp;'+
                            '<span class="comment">'+jsondata[i].commentnum+'</span>'+
                            '<p class="hyperlink">'+jsondata[i].url+'</p>'+
                            '</td>'+
                            '</tr>'+
                            '</table>';

                        if((i + 1) >= jsonLength){
                            // 锁定
                            me.lock();
                            // 无数据
                            me.noData();
                            break;
                        }

                        //var imgs=document.getElementsByName('img');



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
                    // 为了测试，延迟1秒加载
                    setTimeout(function(){
                        $('.lists').append(result);
                        // 每次数据加载完，必须重置
                        me.resetload();
                    },1000);


                },
                error: function(xhr, type){
                    alert('Ajax error!');
                    // 即使加载出错，也得重置
                    me.resetload();
                }
            });
//**************************************************************************************************************


        },
        threshold : 50
    });
});
