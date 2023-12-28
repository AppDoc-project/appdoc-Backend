class InternalServerException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = InternalServerException;