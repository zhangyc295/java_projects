package com.example.common.entity.constants;

public class JudgeConstants {
//
//    public static final String ERROR_ANSWER = "未通过所有测试用例";
//
//    public static final String OUT_OF_MEMORY = "超出内存限制，请优化代码";
//
//    public static final String OUT_OF_TIME = "运行时间超出限制，请优化代码";

    public static final Integer ERROR_SCORE = 0;

    public static final Integer DEFAULT_SCORE = 100;

    public static final String EXAM_CODE_DIR = "user-code";

    public static final String CODE_DIR_POOL = "user-code-pool";

    public static final String DOCKER_USER_CODE_DIR = "/usr/share/java";

    public static final String USER_CODE_JAVA_CLASS_NAME = "Solution.java";

    public static final String USER_CODE_JAVA_FILE_NAME = "Solution";

    public static final String JAVA_ENV_IMAGE = "openjdk:8-jdk-alpine";

    public static final String JAVA_CONTAINER_PREFIX = "/";

    public static final String JAVA_CONTAINER_NAME = "oj-jdk";

    public static final String[] DOCKER_JAVAC_CMD = new String[]{"javac", "/usr/share/java/Solution.java"};

    public static final String[] DOCKER_JAVA_EXEC_CMD = new String[]{"java", "-cp", DOCKER_USER_CODE_DIR, USER_CODE_JAVA_FILE_NAME};



    public static final String UNDERLINE_SEPARATOR = "_";

    // UTF-8 字符集
    public static final String UTF8 = "UTF-8";
}