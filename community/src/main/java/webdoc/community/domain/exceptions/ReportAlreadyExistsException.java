package webdoc.community.domain.exceptions;
/*
* 신고가 이미 진행되어서 발생하는 예외 : deprecated
 */
public class ReportAlreadyExistsException extends RuntimeException {
    public ReportAlreadyExistsException(String message){
        super(message);
    }

    public ReportAlreadyExistsException(Throwable e, String message){
        super(message,e);
    }
}
