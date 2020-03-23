function runRightNow(taskNo) {
    alert(taskNo);
    $.ajax({
        url: "/quartz/runtask/rightnow",
    type: "GET",
        contentType: "application/json",
        data: {"taskNo": taskNo},
    dataType: "json",
        success: function (result) {
        if (result.code == 200) {
            alert("运行成功!");
        } else if (result.code == 6001) {
            alert("任务编号不能为空!");
        } else if (result.code == 1003) {
            alert("无此定时任务!");
        } else {
            alert("执行失败!");
        }
    }
});
};