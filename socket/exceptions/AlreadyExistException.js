// 이미 있는 자원이 있을 때 발생하는 예외
class AlreadyExistException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = AlreadyExistException 