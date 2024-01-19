const axios = require("axios");
const AuthenticationFailException = require("../exceptions/AuthenticationFailException");
const InternalServerError = require("../exceptions/BindingException");



module.exports.fetchUser = async (jwt) => {
    console.log(`http://${process.env.authServer}/auth/server/user/my`);
    try {
        const ret = await axios.get(`http://${process.env.authServer}/auth/server/user/my`, {
            headers: { authorization: jwt }
        });
     
        return ret.data;

    } catch (err) {
        console.log(err);
        if (err.response) {

            const statusCode = err.response.status;
            if (statusCode === 400 || statusCode === 401) {
                // 400 에러 처리
                throw new AuthenticationFailException("인증 실패");
            } else if (statusCode === 500) {
                // 500 에러 처리
                throw new InternalServerError("서버 내부 예외 발생");
            } else {
                // 다른 상태 코드에 대한 처리
                throw new InternalServerError("서버 내부 예외 발생");
            }
        } else if (err.request) {
            // 요청이 전송되지 않은 경우
            throw new InternalServerError("서버 내부 예외 발생");
        } else {
            // 그 외의 에러
            throw new InternalServerError("서버 내부 예외 발생");
        }

      
    }
};

module.exports.fetchUserById = async(jwt,userId) => {
    try {
        const ret = await axios.get(`http://${process.env.authServer}/auth/server/user/id/${userId}`, {
            headers: { authorization: jwt }
        });
     
        return ret.data;

    } catch (err) {

        if (err.response) {

            const statusCode = err.response.status;
            if (statusCode === 401 ||statusCode === 400) {
                // 400 에러 처리
                throw new AuthenticationFailException("인증 실패");
            } else if (statusCode === 500) {
                // 500 에러 처리
                throw new InternalServerError("서버 내부 예외 발생");
            } else {
                // 다른 상태 코드에 대한 처리
                throw new InternalServerError("서버 내부 예외 발생");
            }
        } else if (err.request) {
            // 요청이 전송되지 않은 경우
            throw new InternalServerError("서버 내부 예외 발생");
        } else {
            // 그 외의 에러
            throw new InternalServerError("서버 내부 예외 발생");
        }

      
    }
};