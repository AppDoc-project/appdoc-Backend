const CodeMessageResponse = require("../../response/CodeMessageResponse");
const CommonMessageProvider = require("../../utility/CommonMessageProvider");
const ResponseCodeProvider = require("../../utility/ResponseCodeProvider");

// 요청 url이 매치되지 않을 경우를 핸들링하는 미들웨어

module.exports = (req,res,next) => {
    res.status(404).send(new CodeMessageResponse(CommonMessageProvider.NOT_FOUND,404,ResponseCodeProvider.NOT_FOUND));
};