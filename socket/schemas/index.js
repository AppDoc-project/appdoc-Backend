const mongoose = require("mongoose");

const connect = ()=> {
    if (process.env.NODE_ENV != 'production'){
        mongoose.set('debug',true);
    }
    console.log(`${process.env.ip}연결 시도`);
    mongoose.connect(`mongodb://${process.env.ip}`,
    {
        dbName : 'beatMate'
    });
    
    console.log("몽고 db 연결 성공");
};

mongoose.connection.on("error",(error)=>{
    console.error("몽고디비 연결 에러. 연결을 재시도 합니다",error);
    
   
});
mongoose.connection.on("disconnected",()=>{
    console.error("몽고 디비 연결이 끊어 졌습니다. 연결을 재시도 합니다");
    connect();
});

module.exports = connect;