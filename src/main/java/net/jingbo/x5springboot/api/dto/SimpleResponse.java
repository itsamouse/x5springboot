package net.jingbo.x5springboot.api.dto;

/**
 * Created by Administrator on 2018-5-7.
 */
public class SimpleResponse {
    private String p_ret_code;
    private String p_ret_msg;

    public SimpleResponse() {
    }

    public SimpleResponse(String p_ret_code, String p_ret_msg) {
        this.p_ret_code = p_ret_code;
        this.p_ret_msg = p_ret_msg;
    }

    public SimpleResponse(Object p_ret_code,Object p_ret_msg){
        this.p_ret_code = String.valueOf(p_ret_code);
        this.p_ret_msg = String.valueOf(p_ret_msg);
    }

    public String getP_ret_code() {
        return p_ret_code;
    }

    public void setP_ret_code(String p_ret_code) {
        this.p_ret_code = p_ret_code;
    }

    public String getP_ret_msg() {
        return p_ret_msg;
    }

    public void setP_ret_msg(String p_ret_msg) {
        this.p_ret_msg = p_ret_msg;
    }
}
