// 배열 응답 객체
class ArrayResponse {
    constructor(data, httpStatus) {
      this.data = data;
      this.httpStatus = httpStatus;
      this.size = data.length;
    }
  
    static of(data, httpStatus) {
      return new ArrayResponse(data, httpStatus);
    }
}

module.exports = ArrayResponse;
