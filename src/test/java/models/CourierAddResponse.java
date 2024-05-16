package models;

public class CourierAddResponse {
    private Boolean ok;

    public CourierAddResponse(Boolean ok) {
        this.ok = ok;
    }

    public CourierAddResponse() { }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }
}
