function savetask() {
    var taskno = $("#taskno").val();
    var taskname = $("#taskname").val();
    var schedulerrule = $("#schedulerrule").val();
    var frozenstatus = $("#frozenstatus").val();
    var executorno = $("#executorno").val();
    var sendtype = $("#sendtype").val();
    var url = $("#url").val();
    var executeparamter = $("#executeparamter").val();
    var timekey = $("#timekey").val();
    $.ajax({
        url: "/quartz/add/task",
        dataType: "json",
        type: "POST",
        data: {
            "taskno": taskno,
            "taskname": taskname,
            "schedulerrule": schedulerrule,
            "frozenstatus": frozenstatus,
            "executorno": executorno,
            "sendtype": sendtype,
            "url": url,
            "executeparamter": JSON.stringify(executeparamter),
            "timekey": timekey
        },
        success: function (result) {
            if (result.code == 200) {
                alert("添加成功!");
                location.href = "/";
            } else if (result.code == 1001) {
                alert("该任务编号已经存在");
            }
            else {
                alert("system error");
            }
        }
    });
};