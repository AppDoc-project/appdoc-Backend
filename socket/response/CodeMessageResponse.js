// 코드 & 메시지 응답 객체
class CodeMessageResponse {
    constructor(message, httpStatus, code) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = code;
    }
}

module.exports = CodeMessageResponse;
