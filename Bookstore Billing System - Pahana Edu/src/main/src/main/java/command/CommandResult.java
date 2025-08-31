package command;


public class CommandResult {
    private boolean success;
    private String message;
    private Object data;
    
    public CommandResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public CommandResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    
    // Setters
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setData(Object data) { this.data = data; }
}