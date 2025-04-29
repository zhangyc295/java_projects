package com.example.common.entity.constants;

public class RedisConstants {

    public final static String LOGIN_TOKEN = "login_token:";
    public final static Long TOKEN_EXPIRATION = 24 * 60L; //24 * 60L;
    public final static Long REFRESH_TIME = 30L;

    public final static String TELEPHONE_CODE = "TC:";
    public final static Long CODE_EXPIRATION = 5L; //24 * 60L;

    //每天限制短信验证码请求的次数
    public final static String GET_CODE_TIMES = "GCT:";

    public final static String CONTEST_UNFINISHED_LIST = "CUL:";   //未完赛竞赛
    public final static String CONTEST_FINISHED_LIST = "CFL:";     //已完赛竞赛
    public final static String CONTEST_DETAIL = "CD:";             //竞赛详细信息

    public final static String CLIENT_CONTEST_LIST = "CCL:";       //用户的竞赛列表

    public final static String CLIENT_DETAIL_INFO = "CDI:";       //用户详细信息
    public final static Long CLIENT_INFO_EXPIRATION = 10L;        //用户详细信息
    public final static String CLIENT_UPLOAD_TIMES = "CUT:";      //头像上传次数
    public final static String QUESTION_LIST = "QT:";             //题目id列表
    public final static String CONTEST_QUESTION_LIST = "CQT:";    //竞赛题目id列表
}
