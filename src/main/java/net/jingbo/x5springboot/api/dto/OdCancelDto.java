package net.jingbo.x5springboot.api.dto;

/**
 * Created by Administrator on 2018-5-7.
 */
public class OdCancelDto {
    private String od_number;
    private String  line_number;
    private String cancel_reason;

    public String getOd_number() {
        return od_number;
    }

    public void setOd_number(String od_number) {
        this.od_number = od_number;
    }

    public String getLine_number() {
        return line_number;
    }

    public void setLine_number(String line_number) {
        this.line_number = line_number;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }
}
