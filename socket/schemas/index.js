const mongoose = require("mongoose");

const connect = ()=> {
    if (process.env.NODE_ENV != 'production'){
        mongoose.set('debug',true);
    }
    mongoose.connect(`mongodb://${process.env.ip}`,
    {
        dbName : 'beatMate'
    });
    
    console.log("몽고 db 연결 성공");
};

mongoose.connection.on("error",(error)=>{
    console.error("몽고디비 연결 에러",error);
});
mongoose.connection.on("disconnected",()=>{
    console.rror("몽고 디비 연결이 끊어 졌습니다. 연결을 재시도 합니다");
    connect();
});

module.exports = connect;