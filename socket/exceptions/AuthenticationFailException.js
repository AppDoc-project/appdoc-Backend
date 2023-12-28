class AuthenticationFailException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = AuthenticationFailException;