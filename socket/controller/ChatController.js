const express = require("express");
const router = express.Router();
const jwtAuthenticationFilter = require("./filters/JwtAuthenticationFilter");
const roomCreateRequest = require("../request/RoomCreateRequest");
const chatCreateRequest = require("../request/ChatCreateRequest");
const chatFetchRequest = require("../request/ChatFetchRequest");
const InternalServerException = require("../exceptions/InternalServerException");
const AlreadyExistException = require("../exceptions/AlreadyExistException");
const {createChatRoom, createChat,fetchChatRooms,fetchChats,sendChatToSocket} = require("../service/ChatService");
const CodeMessageResponse = require("../response/CodeMessageResponse");
const CommonMessageProvider = require("../utility/CommonMessageProvider");
const ResponseCodeProvider = require("../utility/ResponseCodeProvider");
const InvalidAccessException = require("../exceptions/InvalidAcessException");
const ArrayResponse = require("../response/ArrayResponse");

// 특정 방에서 채팅을 무한 스크롤링하는 로직
router.get("/",jwtAuthenticationFilter,chatFetchRequest,async(req,res,next)=>{
    const jwt = req.headers.authorization;
    const {limit, count, id} = req.query;

    try{

        const arr = await fetchChats(jwt,req.user.id,id,limit,count);
        res.send(ArrayResponse.of(arr,200));

    }catch(err){

        if (err instanceof AlreadyExistException || err instanceof InvalidAccessException ){

            next(err);

        }else{

            next(new InternalServerException("서버 에러가 발생하였습니다",err));

        }
        
    }


});




// 채팅 방 목록을 가져오는 컨트롤러 로직
router.get("/room",jwtAuthenticationFilter,async(req,res,next)=>{
    const jwt = req.headers.authorization;

    try{

        const arr = await fetchChatRooms(jwt,req.user.id,req.user.isTutor);
        res.send(ArrayResponse.of(arr,200));

    }catch(err){

        if (err instanceof AlreadyExistException || err instanceof InvalidAccessException ){

            next(err);

        }else{

            next(new InternalServerException("서버 에러가 발생하였습니다",err));

        }
        
    }

});



// 채팅 방을 만드는 컨트롤러 로직
router.post("/room",jwtAuthenticationFilter,roomCreateRequest, async(req,res,next)=>{
    const {tuteeId, tutorId} = req.body;
    
    try{
        const jwt = req.headers.authorization;
        const user = req.user;
        if (user.id != tuteeId){
            throw new InvalidAccessException("비상적인 접근입니다");
        }
        await createChatRoom(tuteeId,tutorId, jwt);
    

        res.send(new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.REQUEST_SUCCESS));
        
    }catch(err){

        if (err instanceof AlreadyExistException || err instanceof InvalidAccessException ){

            next(err);

        }else{

            next(new InternalServerException("서버 에러가 발생하였습니다",err));

        }
        
    }

});

// 채팅을 작성하는 컨트롤러 로직
router.post("/",jwtAuthenticationFilter,chatCreateRequest,async(req,res,next)=>{
    const {senderId, receiverId, text} = req.body;
    const user = req.user;
    const jwt = req.headers.authorization;
    try{

        if (user.id != senderId){
            throw new InvalidAccessException("비상적인 접근입니다");
        }
        const socket = req.app.get("io").of("/room");

        const ret = await createChat(senderId,receiverId,text,user.isTutor);
        // userId,senderId,messageId,createdAt,roomId,text,socket,jwt
        await sendChatToSocket(user.id,senderId,ret.createdAt,ret.roomId,text,socket,jwt);

        res.send(new CodeMessageResponse(CommonMessageProvider.REQUEST_SUCCESS,200,ResponseCodeProvider.REQUEST_SUCCESS));

    }catch(err){

        if (err instanceof AlreadyExistException || err instanceof InvalidAccessException ){

            next(err);

        }else{

            next(new InternalServerException("서버 에러가 발생하였습니다",err));

        }
        
    }

});









module.exports = router;
