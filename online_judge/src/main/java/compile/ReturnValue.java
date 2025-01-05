package compile;

import dao.Question;

// 编译返回的状态码
public enum ReturnValue {
    NORMAL(0),
    COMPILE_ERROR(1),
    RUNTIME_ERROR(2),
    QUESTION_NOT_FOUND(-1),
    CODE_INVALID(-2),
    CODE_UNSAFE(-3);
    // error为 0 正常
    // 1 编译出错
    // 2 运行出错
    private final int code;

    ReturnValue(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
