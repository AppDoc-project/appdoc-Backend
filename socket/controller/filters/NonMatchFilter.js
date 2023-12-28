const CodeMessageResponse = require("../../response/CodeMessageResponse");
const CommonMessageProvider = require("../../utility/CommonMessageProvider");
const ResponseCodeProvider = require("../../utility/ResponseCodeProvider");


module.exports = (req,res,next) => {
    res.status(404).send(new CodeMessageResponse(CommonMessageProvider.NOT_FOUND,404,ResponseCodeProvider.NOT_FOUND));
};