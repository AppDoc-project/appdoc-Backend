class BindingException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = BindingException;