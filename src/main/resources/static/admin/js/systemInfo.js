$(function(){
    var websocket = null;
    //开启连接
    var url = "ws://" + window.location.host  + "/systemInfo";
    //判断当前浏览器是否支持WebSocket
    if(websocket == null){
        if('WebSocket' in window){
            websocket = new WebSocket(url);
        }else if('MozWebSocket' in window){
            websocket = new MozWebSocket(url);
        }else{
            websocket = new SockJS(url);
        }
    }
    //连接发生错误的回调方法
    websocket.onerror = function(){}
    //连接成功建立的回调方法
    websocket.onopen = function(event){}
    //接收到消息的回调方法
    websocket.onmessage = function(event){
        var json = $.parseJSON(event.data);
        // 服务器信息
        var serverinfo = json.serverinfo;
        if(serverinfo){
            $("#system-info-1").html("<p class=\"text-left\">本地ip地址："+serverinfo.hostIp+"</p>" +
                "<p class=\"text-left\">本地主机名："+serverinfo.hostName+"</p>" +
                "<p class=\"text-left\">操作系统的名称："+serverinfo.osName+"</p>" +
                "<p class=\"text-left\">操作系统的构架："+serverinfo.arch+"</p>" +
                "<p class=\"text-left\">操作系统的版本："+serverinfo.osVersion+"</p>" +
                "<p class=\"text-left\">JVM可以使用的处理器个数："+serverinfo.processors+"</p>" +
                "<p class=\"text-left\">Java的运行环境版本："+serverinfo.javaVersion+"</p>" +
                "<p class=\"text-left\">Java的运行环境供应商："+serverinfo.vendor+"</p>" +
                "<p class=\"text-left\">Java供应商的URL："+serverinfo.javaUrl+"</p>" +
                "<p class=\"text-left\">Java的安装路径："+serverinfo.javaHome+"</p>" +
                "<p class=\"text-left\">默认的临时文件路径："+serverinfo.tmpdir+"</p>");
        }
        // 内存信息
        var memory = json.memory;
        if(memory){
            $("#system-info-2").html("<p class=\"text-left\">java总内存："+memory.jvmTotal+"</p>" +
                "<p class=\"text-left\">JVM剩余内存："+memory.jvmFree+"</p>" +
                "<p class=\"text-left\">JVM使用内存："+memory.jvmUse+"</p>" +
                "<p class=\"text-left\">JVM使用率："+memory.jvmUsage+"</p>" +
                "<p class=\"text-left\">内存总量："+memory.ramTotal+"</p>" +
                "<p class=\"text-left\">当前内存使用量："+memory.ramUse+"</p>" +
                "<p class=\"text-left\">当前内存剩余量："+memory.ramFree+"</p>" +
                "<p class=\"text-left\">内存使用率："+memory.ramUsage+"</p>" +
                "<p class=\"text-left\">交换区总量："+memory.swapTotal+"</p>" +
                "<p class=\"text-left\">当前交换区使用量："+memory.swapUse+"</p>" +
                "<p class=\"text-left\">当前交换区剩余量："+memory.swapFree+"</p>" +
                "<p class=\"text-left\">交换率："+memory.swapUsage+"</p>");
        }
        // CPU信息
        var cpuInfos = json.cpuInfos;
        if(cpuInfos){
            var str = "";
            $.each(cpuInfos, function(index, value){
                str += ("<p class=\"text-left\">"+index+"用户使用率："+value.cpuUserUse+"</p>" +
                    "<p class=\"text-left\">"+index+"系统使用率："+value.cpuSysUse+"</p>" +
                    "<p class=\"text-left\">"+index+"当前等待率："+value.cpuWait+"</p>" +
                    "<p class=\"text-left\">"+index+"当前空闲率："+value.cpuFree+"</p>" +
                    "<p class=\"text-left\">"+index+"总的使用率："+value.cpuTotal+"</p>");
            });
            $("#system-info-3").html(str);
        }
        // 磁盘信息
        var diskInfos = json.diskInfos;
        if(diskInfos){
            var str = "";
            $.each(diskInfos, function(index, value){
                str += ("<p class=\"text-left\">"+index+"系统盘名称："+value.diskName+"</p>" +
                    "<p class=\"text-left\">"+index+"盘类型："+value.diskType+"</p>" +
                    "<p class=\"text-left\">"+index+"文件系统总大小："+value.diskTotal+"</p>" +
                    "<p class=\"text-left\">"+index+"文件系统剩余大小："+value.diskFree+"</p>" +
                    "<p class=\"text-left\">"+index+"文件系统已经使用量："+value.diskUse+"</p>" +
                    "<p class=\"text-left\">"+index+"内存使用率："+value.diskUsage+"</p>");
            });
            $("#system-info-4").html(str);
        }
    }
    //连接关闭的回调方法
    websocket.onclose = function(){
        websocket = null;
    }
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function(){
        websocket.close();
    }
});