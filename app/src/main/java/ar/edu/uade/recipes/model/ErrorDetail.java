package ar.edu.uade.recipes.model;

import java.util.List;

public class ErrorDetail {
    private List<Object> loc;
    private String msg;
    private String type;

    public List<Object> getLoc() {
        return loc;
    }

    public void setLoc(List<Object> loc) {
        this.loc = loc;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
