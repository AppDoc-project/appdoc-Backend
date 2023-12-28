const BindingException = require("../exceptions/BindingException");

// 채팅 생성을 검증하는 필터
module.exports = (req, res, next) => {
    const { senderId, receiverId, text } = req.body;

    // 모든 필드가 undefined가 아니어야 함
    if (senderId === undefined || receiverId === undefined || text === undefined) {
        throw new BindingException('바인딩 실패');
    }

    // senderId와 receiverId는 정수여야 함
    if (typeof senderId !== 'number' || typeof receiverId !== 'number') {
        throw new BindingException('바인딩 실패');
    }

    // text는 비어있지 않고 300자 이하여야 함
    if (typeof text !== 'string' || text.trim() === '' || text.length > 300) {
        throw new BindingException('바인딩 실패');
    }

    // 모든 검증 통과 시 다음 미들웨어로 이동
    next();
};