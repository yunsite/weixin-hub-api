<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <title>Send Message</title>
    <script type="text/javascript" src="./js/jquery.js"></script>
    <script type="text/javascript" src="./js/jquery.md5.js"></script>

<script type="text/javascript">
function show_reg_acct() {
    $.ajax({
        url: '${pageContext.request.contextPath}/regacct',
        type: 'post',
        dataType: 'json'
    }).done(function(data) {
        $('#acct_id').text(data.acctId);
    });
}
function save_reg_acct() {
    $('#reg_acct_msg').text('');
    if ($('#acct_loginId').val().trim() == '' || $('#acct_passwd').val().trim() == '') {
        $('#reg_acct_msg').text('Please input id and password of weixin account!');
        return;
    }
    
    $.ajax({
        url: '${pageContext.request.contextPath}/regacct',
        data: {
            'save': 'true',
            'loginId': $('#acct_loginId').val().trim(),
            'passwd': $.md5($('#acct_passwd').val().trim().substr(0, 16))
        },
        type: 'post',
        dataType: 'json'
    }).done(function(data) {
        $('#reg_acct_msg').text(data.msg);
        
        show_reg_acct();
    });
}
function single_msg() {
    $('#single_send_msg').text('');
    if ($('#acct_id').text().trim() == '') {
        $('#single_send_msg').text('Register account first!');
        return;
    }
    else if ($('#single_fakeIds').val().trim() == '' || $('#single_content').val().trim() == '') {
        $('#single_send_msg').text('Please input target fake and content');
        return;
    }
    
    $.ajax({
        url: '${pageContext.request.contextPath}/singlesend',
        data: {
            'fakeIds': $('#single_fakeIds').val().trim(),
            'content': $('#single_content').val().trim()
        },
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        type: 'POST',
        dataType: 'json'
    }).done(function(data) {
        $('#single_send_msg').text(data.msg);
    });
}
function mass_msg() {
    $('#mass_send_msg').text('');
    if ($('#acct_id').text().trim() == '') {
        $('#mass_send_msg').text('Register account first!');
        return;
    }
    else if ($('#mass_content').val().trim() == '') {
        $('#mass_send_msg').text('Please input content');
        return;
    }
    
    var formData = {'content': $('#mass_content').val().trim()};
    if ($('#mass_groupId').val().trim() != '')
        formData['groupId'] = $('#mass_groupId').val().trim();
    
    $.ajax({
        url: '${pageContext.request.contextPath}/masssend',
        data: formData,
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        type: 'POST',
        dataType: 'json'
    }).done(function(data) {
        $('#mass_send_msg').text(data.msg);
    });
}

$(function() {
    show_reg_acct();
});
</script>
</head>

<body>
<div id="acct_info">
    Registered Account: <span id="acct_id"></span>
</div>
<br/>

<div id="reg_acct">
    <h2>Register WeiXin Account</h2>
    <div>Login ID: <input type="text" id="acct_loginId"></div>
    <div>Password: <input type="password" id="acct_passwd"></div>
    <div>
        <input type="button" value="Register" onclick="save_reg_acct();">
        <span id="reg_acct_msg"></span>
    </div>
</div>
<br/>

<div id="single_msg">
    <h2>Send Single Message</h2>
    <div>Target Fake: <input type="text" id="single_fakeIds"></div>
    <div>Msg Content: <textarea rows="3" cols="50" id="single_content"></textarea></div>
    <div>
        <input type="button" value="Send" onclick="single_msg();">
        <span id="single_send_msg"></span>
    </div>
</div>
<br/>

<div id="mass_msg">
    <h2>Send Mass Message</h2>
    <div>Group ID: <input type="text" id="mass_groupId"></div>
    <div>Msg Content: <textarea rows="3" cols="50" id="mass_content"></textarea></div>
    <div>
        <input type="button" value="Send" onclick="mass_msg();">
        <span id="mass_send_msg"></span>
    </div>
</div>
</body>
</html>