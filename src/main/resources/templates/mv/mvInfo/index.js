function openPlay(num, quality) {
    const dp = new DPlayer({
        container: document.getElementById('dplayer'),
        screenshot: true,
		mutex: true,
		hotkey: true,
        video: {
            quality: quality,
            defaultQuality: 0
        },
		danmaku: {
            id: 'SongM_VideoInfoId_'+getUrlParam('videoInfoId')+'_'+num,
            api: 'https://api.prprpr.me/dplayer/',
            token: '2333',
            maximum: 1000,
            user: 'songm',
            bottom: '15%'
        }
    })
}
function playNum(num, video) {
    var m3u8PlayUrl = video.m3u8PlayUrl;
    var m3u8Format = video.m3u8Format;
    var json_arr = [];
    for (var p in m3u8Format) {
        var json = {};
        json['name'] = p;
        json['url'] = m3u8PlayUrl + m3u8Format[p];
        json['type'] = 'hls';
        if (p != 'free') {
            json_arr.push(json)
        }
    }
    openPlay(num, json_arr)
}
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null
}
function getMvInfo(num) {
    $.ajax({
        url: 'http://api.hbzjmf.com/api/app/video/ver2/video/searchVideoInfoDetail_v2_1/2/7?videoInfoId=' + getUrlParam('videoInfoId'),
        type: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-Client-NonceStr': 'WXhRUam8J4',
            'X-Client-TimeStamp': '1542003450801',
            'X-Client-Version': '2.1.0',
            'X-Client-Sign': '747d4b1f7c5e5ebc7bf37bda33488dd0e1faff08be7490b6b127e1ee4c5c52ce',
            'X-Auth-Token': 'mb_token:18182247:3585e97d7cb3681b0514e99201ce0dac'
        },
        success: function(json) {
            if (json.success) {
                $('#mv_content').text("");
                $('#play_num').text("");
                var data = json.data;
                // 设置title
				$("title").html(data.title+' 第'+(num+1)+'集');
				// 视频内容
				$('#mv_content').append(
					'<div id="post-content" class="post-content">'+
					'<p>名称: '+data.title+'</p>'+
					'<p>豆瓣评分: '+data.doubanScore+'</p>'+
					'<p>分类: '+data.classifyTypeList+'</p>'+
					'<p>导演: '+data.director+'</p>'+
					'<p>演员: '+data.staring+'</p>'+
					'----------------------------------------------------------------------------------------'+
					'<p>简介: '+data.intro+'</p>'+
					'</div><br/>'
				);
				var videoList = data.videoList;
                var coverUrl = data.coverUrl;
                for (var v in videoList) {
                    if (v % 7 == 0) {
                        $('#play_num').append("<p></p>")
                    }
                    var video = videoList[v];
                    if (v == num) {
                        $('#play_num').append('&nbsp;<button style="width: 40px;color: #fff;background-color: #f0ad4e;border-color: #eea236;padding: 6px 12px;cursor: pointer;border-radius: 4px;" type="button" onclick="getMvInfo(' + v + ');">' + video.sortNum + '</button>&nbsp;')
                    } else {
                        $('#play_num').append('&nbsp;<button style="width: 40px;color: #fff;background-color: #337ab7;border-color: #2e6da4;padding: 6px 12px;cursor: pointer;border-radius: 4px;" type="button" onclick="getMvInfo(' + v + ');">' + video.sortNum + '</button>&nbsp;')
                    }
                }
                playNum(num, videoList[num])
            } else {
                alert(json.description)
            }
        }
    })
}
$(function() {
    getMvInfo(0)
});
