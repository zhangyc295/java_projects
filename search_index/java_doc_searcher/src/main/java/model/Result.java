package model;

//对文档修改后的结果 把内容变为摘要（缩短显示内容）
public class Result {
    private String title;
    private String url;
    private String describe;

    @Override
    public String toString() {
        return "model.Result{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", describe='" + describe + '\'' +
                '}';
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // 正文的一段摘要
}
