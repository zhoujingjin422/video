package com.ruifenglb.www;

public class ApiConfig {

    public static final String HTTP_URL=""; //动态域名，推荐七牛储存免费10G基本够用

    public static  String MOGAI_BASE_URL = "https://oohapigou003.top";//可以是http也可以是https结尾不能带/


    public static final String getStart = "/mogai_api.php/v1.main/startup";
    public static final String getTypeList = "/mogai_api.php/v1.vod/types";
    public static final String getBannerList = "/mogai_api.php/v1.vod";

    //专题
    public static final String getTopicList = "/mogai_api.php/v1.topic/zhuantiList";
    //专题详情
    public static final String getTopicDetail = "/mogai_api.php/v1.topic/topicDetail";
    //游戏
    public static final String getGameList = "/mogai_api.php/v1.youxi/index";
    //添加视频播放记录
    public static final String addPlayLog = "/mogai_api.php/v1.name/addViewLog";
    //上报观影时长
    public static final String watchTimeLong = "/mogai_api.php/v1.name/viewSeconds";
    //获取视频播放记录
    public static final String getPlayLogList = "/mogai_api.php/v1.name/viewLog";
    //获取视频播放进度
    public static final String getVideoProgress = "/mogai_api.php/v1.vod/videoProgress";
    //删除播放记录
    public static final String dleltePlayLogList = "/mogai_api.php/v1.name/delVlog";
    //直播列表
    public static final String getLiveList = "/mogai_api.php/v1.zhibo";
    //直播详情
    public static final String getLiveDetail = "/mogai_api.php/v1.zhibo/detail";
    //播放器开始播放广告
    public static final String getAdconfig = "/application/api/controller/v1/mogai_ad.php";  //老版本使用这个接口

    //找回密码短信发送
    public static final String findpasssms = "/mogai_api.php/v1.auth/findpasssms";
    //找回密码
    public static final String findpass = "/mogai_api.php/v1.auth/findpass";



    public static final String getTopList = "/mogai_api.php/v1.vod";
    public static final String getCardList = "/mogai_api.php/v1.main/category";
    public static final String getRecommendList = "/mogai_api.php/v1.vod/vodPhbAll";
    public static final String getCardListByType = "/mogai_api.php/v1.vod/type";
    public static final String getVodList = "/mogai_api.php/v1.vod";
    public static final String getVod = "/mogai_api.php/v1.vod/detail";

    public static final String COMMENT = "/mogai_api.php/v1.comment";

    public static final String USER_INFO = "/mogai_api.php/v1.name/detailUVE3MjQyNDg0";
    public static final String LOGIN = "/mogai_api.php/v1.auth/login";
    public static final String LOGOUT = "/mogai_api.php/v1.auth/logout";
    public static final String REGISTER = "/mogai_api.php/v1.auth/register";
    public static final String VERIFY_CODE = "/mogai_api.php/v1.auth/registerSms";//发送注册验证码
    public static final String VERIFY_CODE_FIND = "/mogai_api.php/v1.auth/findpasssms";//发送找回密码验证码
    public static final String OPEN_REGISTER = "/mogai_api.php/v1.name/phoneReg";
    public static final String SIGN = "/mogai_api.php/v1.sign";
    public static final String GROUP_CHAT = "/mogai_api.php/v1.groupchat";
    public static final String CARD_BUY = "/mogai_api.php/v1.name/buy";
    public static final String UPGRADE_GROUP = "/mogai_api.php/v1.name/group";
    public static final String SCORE_LIST = "/mogai_api.php/v1.name/groups";
    public static final String CHANGE_AGENTS = "/mogai_api.php/v1.name/changeAgents";
    public static final String AGENTS_SCORE = "/mogai_api.php/v1.name/agentsScore";
    public static final String POINT_PURCHASE = "/mogai_api.php/v1.name/order";
    public static final String CHANGE_NICKNAME = "/mogai_api.php/v1.name";
    public static final String CHANGE_AVATOR = "/mogai_api.php/v1.upload/user";
    public static final String GOLD_WITHDRAW = "/mogai_api.php/v1.name/goldWithdrawApply";
    public static final String PAY_TIP = "/mogai_api.php/v1.name/payTip";
    public static final String GOLD_TIP = "/mogai_api.php/v1.name/goldTip";
    public static final String FEEDBACK = "/mogai_api.php/v1.gbook";
    public static final String COLLECTION_LIST = "/mogai_api.php/v1.name/favs";
    public static final String COLLECTION = "/mogai_api.php/v1.name/ulog";
    public static final String SHARE_SCORE = "/mogai_api.php/v1.name/shareScore";
    public static final String TASK_LIST = "/mogai_api.php/v1.name/task";
    public static final String MSG_LIST = "/mogai_api.php/v1.message/index";
    public static final String MSG_DETAIL = "/mogai_api.php/v1.message/detail";
    public static final String EXPAND_CENTER = "/mogai_api.php/v1.name/userLevelConfig";
    public static final String MY_EXPAND = "/mogai_api.php/v1.name/subUsers";
    public static final String SEND_DANMU = "/mogai_api.php/v1.danmu";
    public static final String SCORE = "/mogai_api.php/v1.vod/score";
    public static final String CHECK_VOD_TRYSEE = "/mogai_api.php/v1.name/checkVodTrySee";
    public static final String BUY_VIDEO = "/mogai_api.php/v1.name/buypopedom";
    public static final String CHECK_VERSION = "/mogai_api.php/v1.main/version";
    public static final String PAY = "/mogai_api.php/v1.name/pay";
    public static final String ORDER = "/mogai_api.php/v1.name/order";
    public static final String APP_CONFIG = "/mogai_api.php/v1.name/appConfig";
    public static final String SHARE_INFO = "/mogai_api.php/v1.name/shareInfo";
    public static final String video_count = "/mogai_api.php/v1.vod/videoViewRecode";
    public static final String tabFourInfo = "/mogai_api.php/v1.youxi/index";
    public static final String tabThreeName = "/mogai_api.php/v1.zhibo/thirdUiName";
    public static final String getRankList = "/mogai_api.php/v1.vod/vodphb";
    public static final String ADD_GROUP = "/mogai_api.php/v1.name/addGroup";
    public static final String getDanMuList = "/mogai_api.php/v1.danmu/index";//弹幕列表

    public static void main() {

    }

}
