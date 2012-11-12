/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.vm;

/**
 *
 * @author nile
 */
public class Result {

    private String state;
    private String code;
    private String msg;
    private Object data;

    private Result() {
    }

    public static Result ok(String code, String msg, Object data) {
        return one("ok", code, msg, data);
    }

    public static Result failed(String code, String msg, Object data) {
        return one("failed", code, msg, data);
    }

    public static Result one(String state, String code, String msg, Object data) {
        Result res = new Result();
        res.state = state;
        res.code = code;
        res.msg = msg;
        res.data = data;
        return res;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
