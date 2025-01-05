package compile;

public class Result {
    // returnValue表示返回的状态码
    private ReturnValue returnValue;

    // 异常的原因
    private String Reason;

    // 输出的结果
    private String stdout;

    // 错误的结果
    private String stderr;

    public ReturnValue getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ReturnValue returnValue) {
        this.returnValue = returnValue;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    @Override
    public String toString() {
        return "compile.Result{" +
                "returnValue = " + returnValue +
                ", Reason = '" + Reason + '\'' +
                ", stdout = '" + stdout + '\'' +
                ", stderr = " + stderr +
                '}';
    }
}
