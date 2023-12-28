class AlreadyExistException extends Error {
    constructor(message, error) {
        super(message);
        this.error = error;
    }
}

module.exports = AlreadyExistException 