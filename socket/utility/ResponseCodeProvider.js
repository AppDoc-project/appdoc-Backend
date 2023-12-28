const ResponseCodeProvider = {
    SUCCESS: 200,
    TUTEE_LOGIN: 201,
    TUTOR_LOGIN: 202,
    BINDING_FAILURE: 400,
    VALIDATION_EXPIRED: 401,
    WRONG_CODE: 402,
    LOGIN_FAIL: 403,
    EMAIL_EXISTS: 404,
    INVALID_ACCESS: 405,
    AUTHENTICATION_DENIED: 406,
    AUTHENTICATION_ONGOING: 407,
    AUTHENTICATION_NOT_PROVIDED: 408,
    NOT_AUTHORIZED: 409,
    ALREADY_EXISTS: 410,
    NOT_FOUND: 412,
    INTERNAL_SERVER_ERROR: 500,
    BANNED: 411,
};

module.exports = ResponseCodeProvider;
