package compile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 每一个过程
public class Task {
    // 相对路径 private static final String dir = "./tmp/";
    private String dir = "E:/Javaprojects/online_judge/tmp/";
    private String className = "Solution";
    //编译代码类名
    private String codeName = dir + className + ".java";
    //编译时错误
    private String compileError = dir + "compileError.txt";
    //标准输出
    private String stdOut = dir + "stdOut.txt";
    //运行时错误
    private String stdErr = dir + "stdErr.txt";

    public Task() {
        // this.dir = "E:/Javaprojects/online_judge/tmp/" + UUID.randomUUID() + "/";
        // 相对路径
        this.dir = "./tmp/" + UUID.randomUUID() + "/";

        this.className = "Solution";
        this.codeName = dir + "Solution.java";
        this.compileError = dir + "compileError.txt";
        this.stdOut = dir + "stdOut.txt";
        this.stdErr = dir + "stdErr.txt";
    }

    // 输入java源代码   返回编译运行的结果
    public Result compileRun(SrcCode srcCode) {
        Result result = new Result();
        File workingDir = new File(dir);
        if (!workingDir.exists()) {
            workingDir.mkdirs();
        }
        if (!checkCode(srcCode.getCode())){
            System.out.println("用户提交的代码具有风险！");
            result.setReason("您提交的代码具有风险！");
            result.setReturnValue(ReturnValue.CODE_UNSAFE);
            return result;
        }
        // 将 compile.SrcCode 中的 code 写入文件，且类名与文件名相同 (Solution.java)
        FileOperation.writeFile(codeName, srcCode.getCode());
        // 构造编译命令  javac -encoding utf8 ./tmp/Solution.java -d ./tmp/
        String compileCommand = String.format("javac -encoding utf8 %s -d %s", codeName, dir);
        System.out.println("compileCommand" + compileCommand);
        // 这里是编译过程 关注编译失败的原因
        // 编译成功 文件为空
        Method.run(compileCommand, null, compileError);
        String compileResult = FileOperation.readFile(compileError);
        if (!compileResult.isEmpty()) {
            System.out.println("编译错误");
            result.setReturnValue(ReturnValue.COMPILE_ERROR);
            result.setReason(compileResult);
            return result;
        }
        // System.out.println(compileError);
        // 子进程调用命令并执行，返回 compile.Result 对象
        String runCommand = String.format("java -classpath %s %s", dir, className);
        System.out.println("runCommand" + runCommand);
        Method.run(runCommand, stdOut, stdErr);
        // 如果运行时抛出异常 则运行出错
        String runError = FileOperation.readFile(stdErr);
        if (!runError.isEmpty()) {
            System.out.println("运行错误");
            result.setReturnValue(ReturnValue.RUNTIME_ERROR);
            result.setReason(runError);
            result.setStderr(stdErr);
            return result;
        }
        // 成功编译和运行
        System.out.println("运行成功");
        result.setReturnValue(ReturnValue.NORMAL);
        result.setStdout(FileOperation.readFile(stdOut));
        return result;
    }

    // 防止恶意代码
    private boolean checkCode(String code) {
        List<String> blackList = new ArrayList<String>();
        blackList.add("Runtime");
        blackList.add("exec");
        blackList.add("java.io");
        for (String word : blackList) {
            if (code.contains(word)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Task task = new Task();
        SrcCode srcCode = new SrcCode();
        srcCode.setCode("public class Solution {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello\");\n" +
                "    }\n" +
                "}");
        Result result = task.compileRun(srcCode);
        System.out.println(result);
    }
}
