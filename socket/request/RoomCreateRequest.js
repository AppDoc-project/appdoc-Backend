const BindingException = require("../exceptions/BindingException");

// 채팅방 생성을 검증하는 필터
module.exports = (req, res, next) => {
    const { tutorId, tuteeId } = req.body;

    // tutorId가 숫자이고 undefined가 아닌지 검증
    if (typeof tutorId !== 'number' || tutorId === undefined) {
        // BindingException 던지기
        next(new BindingException('값 검증 실패'));
    }

    // tuteeId가 숫자이고 undefined가 아닌지 검증
    if (typeof tuteeId !== 'number' || tuteeId === undefined) {
        // BindingException 던지기
        next(new BindingException('값 검증 실패'));
    }

    // 모든 검증 통과 시 다음 미들웨어로 이동
    next();
};