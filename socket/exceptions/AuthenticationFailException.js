// 인증 실패시 발생하는 예외

class AuthenticationFailException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = AuthenticationFailException;