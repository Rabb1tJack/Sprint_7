package models;

public class FailedResponse {
    private String message;

    public FailedResponse() {
    }

    public FailedResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
