// 바인딩 실패시 발생하는 예외

class BindingException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = BindingException;