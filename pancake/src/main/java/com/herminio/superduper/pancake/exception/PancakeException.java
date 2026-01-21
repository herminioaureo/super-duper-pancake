package com.herminio.superduper.pancake.exception;

public class PancakeException extends Exception {

    String errorCode = null;

    public PancakeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PancakeException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PancakeException [errorCode=")
          .append(errorCode)
          .append(", message=")
          .append(getMessage())
          .append(", stackTrace=");
        
        if (getCause() != null) {
            for (StackTraceElement element : getCause().getStackTrace()) {
                sb.append("\n\t").append(element.toString());
            }
        } else {
            for (StackTraceElement element : getStackTrace()) {
                sb.append("\n\t").append(element.toString());
            }
        }
        
        sb.append("]");
        return sb.toString();
    }



    
}
