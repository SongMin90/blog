$(function(){
    sys_serverinfo();
    sys_memory();
    sys_cpuInfos();
    sys_diskInfos();
    setInterval(sys_serverinfo, 1000 * 60);
    setInterval(sys_memory, 1000 * 1);
    setInterval(sys_cpuInfos, 1000 * 1);
    setInterval(sys_diskInfos, 1000 * 1);
    function sys_serverinfo(){
        $.ajax({
            url: '/admin/system/serverinfo',
            success: function (result) {
                if(result.success){
                    var payload = result.payload;
                    $("#system-info-1").html("<p class=\"text-left\">本地ip地址："+payload.hostIp+"</p>" +
                            "<p class=\"text-left\">本地主机名："+payload.hostName+"</p>" +
                            "<p class=\"text-left\">操作系统的名称："+payload.osName+"</p>" +
                            "<p class=\"text-left\">操作系统的构架："+payload.arch+"</p>" +
                            "<p class=\"text-left\">操作系统的版本："+payload.osVersion+"</p>" +
                            "<p class=\"text-left\">JVM可以使用的处理器个数："+payload.processors+"</p>" +
                            "<p class=\"text-left\">Java的运行环境版本："+payload.javaVersion+"</p>" +
                            "<p class=\"text-left\">Java的运行环境供应商："+payload.vendor+"</p>" +
                            "<p class=\"text-left\">Java供应商的URL："+payload.javaUrl+"</p>" +
                            "<p class=\"text-left\">Java的安装路径："+payload.javaHome+"</p>" +
                            "<p class=\"text-left\">默认的临时文件路径："+payload.tmpdir+"</p>");
                }
            }
        });
    }
    function sys_memory(){
        $.ajax({
            url: '/admin/system/memory',
            success: function (result) {
                if(result.success){
                    var payload = result.payload;
                    $("#system-info-2").html("<p class=\"text-left\">java总内存："+payload.jvmTotal+"</p>" +
                        "<p class=\"text-left\">JVM剩余内存："+payload.jvmFree+"</p>" +
                        "<p class=\"text-left\">JVM使用内存："+payload.jvmUse+"</p>" +
                        "<p class=\"text-left\">JVM使用率："+payload.jvmUsage+"</p>" +
                        "<p class=\"text-left\">内存总量："+payload.ramTotal+"</p>" +
                        "<p class=\"text-left\">当前内存使用量："+payload.ramUse+"</p>" +
                        "<p class=\"text-left\">当前内存剩余量："+payload.ramFree+"</p>" +
                        "<p class=\"text-left\">内存使用率："+payload.ramUsage+"</p>" +
                        "<p class=\"text-left\">交换区总量："+payload.swapTotal+"</p>" +
                        "<p class=\"text-left\">当前交换区使用量："+payload.swapUse+"</p>" +
                        "<p class=\"text-left\">当前交换区剩余量："+payload.swapFree+"</p>" +
                        "<p class=\"text-left\">交换率："+payload.swapUsage+"</p>");
                }
            }
        });
    }
    function sys_cpuInfos(){
        $.ajax({
            url: '/admin/system/cpuInfos',
            success: function (result) {
                if(result.success){
                    var str = "";
                    $.each(result.payload, function(index, value){
                        str += ("<p class=\"text-left\">"+index+"用户使用率："+value.cpuUserUse+"</p>" +
                        "<p class=\"text-left\">"+index+"系统使用率："+value.cpuSysUse+"</p>" +
                        "<p class=\"text-left\">"+index+"当前等待率："+value.cpuWait+"</p>" +
                        "<p class=\"text-left\">"+index+"当前空闲率："+value.cpuFree+"</p>" +
                        "<p class=\"text-left\">"+index+"总的使用率："+value.cpuTotal+"</p>");
                    });
                    $("#system-info-3").html(str);
                }
            }
        });
    }
    function sys_diskInfos(){
        $.ajax({
            url: '/admin/system/diskInfos',
            success: function (result) {
                if(result.success){
                    var str = "";
                    $.each(result.payload, function(index, value){
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
        });
    }
})