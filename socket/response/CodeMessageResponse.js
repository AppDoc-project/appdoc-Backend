
class CodeMessageResponse {
    constructor(message, httpStatus, code) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = code;
    }
}

module.exports = CodeMessageResponse;
