项目说明

    最近了解了一下微信公众平台开放的API，发现到目前为止，腾讯并未
公开向微信用户发送消息（单个或群发）的接口，只能登录公众平台的后台
管理系统，操作界面发送消息，不便于那些业务及时通知类的应用使用。因
此做了几个消息发送的API，方便使用。
    消息发送实现的机制也很简单，组织HTTP数据包模拟浏览器向公众平台
发送请求，起到代替手工操作的目的。

功能说明
    1.自动登录公众平台账号，保存token数据（不能处理图片验证码）
    2.通过API，向用户单个或群发消息
    3.只支持文本消息


API使用
    代码很简单，看看就明白了。
    API调用可参见wxhub.web下的几个servlet，基本涉及两个class：
wxhub.auth.IAuthManager, wxhub.msg.IMsgManager，先把微信公共平台
的后台账号信息注册进AuthManager，再调用MsgManager的两个方法：
    sendSingleMsg   单个用户发送消息
    sendMassMsg     群发消息



项目依赖
    http-client
    json-lib
