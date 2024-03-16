const AlreadyExistException = require("../../exceptions/AlreadyExistException");
const AuthenticationFailException = require("../../exceptions/AuthenticationFailException");
const BindingException = require("../../exceptions/BindingException");
const InvalidAccessException = require("../../exceptions/InvalidAcessException");
const CodeMessageResponse = require("../../response/CodeMessageResponse")
const CommonMessageProvider = require("../../utility/CommonMessageProvider");
const ResponseCodeProvider = require("../../utility/ResponseCodeProvider");

// 예외를 최종 처리하는 미들웨어 
module.exports = (err, req, res, next) => {

    console.log(err);

    
    if (err instanceof AuthenticationFailException){
        
        res.status(401).send(new CodeMessageResponse(CommonMessageProvider.LOGIN_FAIL,401,ResponseCodeProvider.LOGIN_FAIL));

    } else if(err instanceof BindingException){

        res.status(400).send(new CodeMessageResponse(CommonMessageProvider.BINDING_FAILURE,400,ResponseCodeProvider.BINDING_FAILURE));
    
    } else if(err instanceof AlreadyExistException){

        res.status(400).send(new CodeMessageResponse(CommonMessageProvider.ALREADY_EXISTS,400,ResponseCodeProvider.ALREADY_EXISTS));

    } else if(err instanceof InvalidAccessException){

        res.status(400).send(new CodeMessageResponse(CommonMessageProvider.INVALID_ACCESS,400,ResponseCodeProvider.INVALID_ACCESS));

    } else{

        res.status(500).send(new CodeMessageResponse(CommonMessageProvider.INTERNAL_SERVER_ERROR,500,ResponseCodeProvider.INTERNAL_SERVER_ERROR));

    }
}