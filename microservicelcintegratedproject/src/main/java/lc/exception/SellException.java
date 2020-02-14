package lc.exception;

/**
 * @author liuchaoOvO on 2019/5/22
 */
public class SellException extends Throwable{
    private String msg;
    private int i;
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public SellException(int i,String msg) {
        this.i=i;
        this.msg = msg;
    }
    public SellException(String msg) {
        this.msg = msg;
    }

    public SellException() {
    }

}
