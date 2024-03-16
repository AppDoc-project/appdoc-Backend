// 비정상적인 접근 시 발생하는 예외
class InvalidAccessException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = InvalidAccessException;