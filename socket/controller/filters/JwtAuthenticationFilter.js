const jwt = require("jsonwebtoken");
const AuthenticationFailException = require("../../exceptions/AuthenticationFailException");
const InternalServerException = require("../../exceptions/BindingException");
const {fetchUser} = require("../../service/UserService");
const ResponseCodeProvider = require("../../utility/ResponseCodeProvider");

// 로그인을 위한 미들웨어
module.exports = async (req, res, next) => {
    try{
        req.decoded = jwt.verify(req.headers.authorization, process.env.JWT_SECRET);
        const user = await fetchUser(req.headers.authorization);
        req.user = user;
        return next();

    } catch(error){
        if (error instanceof InternalServerException){
            next(error);
        }else{
            next(new AuthenticationFailException("인증에 실패 하였습니다"));
        }
    }
}; 

