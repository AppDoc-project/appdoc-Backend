package webdoc.community.domain.exceptions;

public class ReportAlreadyExistsException extends RuntimeException {
    public ReportAlreadyExistsException(String message){
        super(message);
    }

    public ReportAlreadyExistsException(Throwable e, String message){
        super(message,e);
    }
}
