const BindingException = require("../exceptions/BindingException");

// 채팅 fetch 요청을 검증하는 필터
module.exports = (req, res, next) => {
    const {id} = req.query;
  
    // limit, count가 undefined가 아니고 id가 문자열이며 비어 있지 않은지 검증
    if (typeof id === 'string' && id.trim() !== '') {
      next(); // 조건을 만족하면 다음 미들웨어로 이동
    } else {
      // 조건을 만족하지 않으면 예외 throw
      throw new BindingException("바인딩 실패");
    }
};