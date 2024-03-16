const mongoose = require("mongoose");
const logger = require("../logger");

// 몽고 DB 연결 

const connect = ()=> {
    if (process.env.NODE_ENV != 'production'){
        mongoose.set('debug',true);
    }
    
    logger.info(`${process.env.ip}연결 시도`);
    mongoose.connect(`mongodb://${process.env.ip}`,
    {
        dbName : 'beatMate',
        maxPoolSize: 10
    });
    
    logger.info("몽고 db 연결 성공");
};

mongoose.connection.on("error",(error)=>{
    logger.error("몽고디비 연결 에러. 연결을 재시도 합니다",error);
    
   
});
mongoose.connection.on("disconnected",()=>{
    logger.error("몽고 디비 연결이 끊어 졌습니다.");
});

module.exports = connect;