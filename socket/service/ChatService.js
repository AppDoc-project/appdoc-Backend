const InvalidAccessException = require("../exceptions/InvalidAcessException");
const {fetchUserById,fetchUser} = require("./UserService");
const room = require("../schemas/room");
const AlreadyExistException = require("../exceptions/AlreadyExistException");
/*
* 채팅 서비스 
*/

// 방 정보를 응답객체로 변환
const mapToRoomResponse = async (arr, isTutor, jwt) => {
    const response = [];
  
    for (let i = 0; i < arr.length; i++) {
      const { tuteeId, tutorId, messages, _id } = arr[i];
      const targetId = isTutor ? tuteeId : tutorId;
      const userId = isTutor ? tutorId : tuteeId;
      let readCount=0;
      
      messages.forEach(ele=>{
          // 내가 보낸게 아닐 때
        if((ele.senderId != userId && ele.isRead === true) || ele.senderId === userId){
            readCount += 1;
        }
      });

      const ret = await fetchUserById(jwt, targetId);
  
      const temp = {
        id: _id,
        target: {
          "name": ret.nickName,
          "userId": ret.id,
          "profile": ret.profile
        },
        notReadYet: messages.length - readCount,
        lastMessage: messages.length > 0 ? messages[messages.length - 1].content : null,
        lastTime: messages.length > 0 ? messages[messages.length - 1].createdAt : null
      };
  
      response.push(temp);
    }
  
    // lastTime이 null인 경우 우선순위를 가장 높게 정렬
    response.sort((a, b) => {
      if (a.lastTime === null && b.lastTime === null) return 0;
      if (a.lastTime === null) return -1;
      if (b.lastTime === null) return 1;
      return new Date(b.lastTime) - new Date(a.lastTime);
    });

    response.forEach(e=>{
        if(e.lastTime){
            e.lastTime = dateFormat(e.lastTime);
        }
        
    });
  
    return response;
  };

  // 채팅 정보를 응답객체로 전환
  const mapToChatResponse = async (arr,jwt,userId) => {
    const response = [];
    for (let i = 0; i<arr.length; i++){

        let sender,name;

        const {_id, senderId, createdAt, content} = arr[i];
        if (senderId == userId){
            sender = await fetchUser(jwt);
            name = sender.name;
        }else{
            sender = await fetchUserById(jwt,senderId);
            name = sender.nickName;
        }

        const temp = {
            id : _id,
            content : content,
            createdAt : createdAt,
            sender : 
                {
                    name : name,
                    profile : sender.profile,
                    userId : sender.id
                }
        };

        response.push(temp);
    }
  
    response.forEach(e=>{
        e.createdAt = dateFormat(e.createdAt);
    });

    return response;

  };

  // 채팅을 몽고DB에서 가져온다
  module.exports.fetchChats = async (jwt, userId, roomId, limit, count) => {
    let ret = await room.findById(roomId);
  
    if (!ret) {
      throw new InvalidAccessException("비정상적인 접근입니다");
    }
  
    if (ret.tuteeId !== userId && ret.tutorId !== userId) {
      throw new InvalidAccessException("비정상적인 접근입니다");
    }
  
    let messages = ret.messages || [];

  
    // messages를 createdAt 기준으로 내림차순으로 정렬
    messages.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  
    // count 개수만큼 제거
    messages = messages.slice(count);
  
    // limit 개수만큼 반환
    const limitedMessages = messages.slice(0, limit);
    return mapToChatResponse(limitedMessages,jwt,userId);
  };

// 자기가 속한 채팅방을 가져오는 로직
module.exports.fetchChatRooms = async (jwt,userId,isTutor) => {
    let ret;
    if (isTutor){
        ret = await room.find({"tutorId":userId});

    }else{
        ret = await room.find({"tuteeId":userId});
    }


    return mapToRoomResponse(ret,isTutor,jwt);    


};

// 모든 채팅을 읽음 처리한다

module.exports.readChat = async(chatId,roomId,userId)=>{
    const existingRoom = await room.findById(roomId);

    if (!existingRoom){
        throw new InvalidAccessException("채팅방이 존재하지 않습니다");
    }

    if (existingRoom.tutorId != userId && existingRoom.tuteeId != userId){
        console.log(tutorId,tuteeId,userId);
        throw new InvalidAccessException("비정상적인 접근 입니다");
    }

    existingRoom.messages.forEach(ele=>{
        if(ele._id.toString() === chatId){
            ele.isRead = true;
        } 
    });

    await existingRoom.save();

}

// 외부소켓에 메세지 전송 receiverId,senderId,ret.createdAt,ret.roomId,text,socket,jwt
module.exports.sendChatToOuterSocket = async(receiverId,senderId,createdAt,roomId,text,socket,jwt) =>{


    let sender,name; 
    sender = await fetchUserById(jwt,senderId);
    name = sender.nickName;

    const message = {
        content : text,
        createdAt : dateFormat(createdAt),
        sender : 
            {
                name : name,
                profile : sender.profile,
                userId : sender.id
            },
        id : roomId
    };
    
    socket.to(receiverId).emit("message",message);


};


// 채팅방을 만드는 로직
module.exports.createChatRoom = async (tuteeId,tutorId,jwt) => {
    const tutor = await fetchUserById(jwt,tutorId);
    
    if (tutor.isTutor === undefined || tutor.isTutor === false){
        throw new InvalidAccessException("비정상적인 접근입나다");
    }

    let ret = await room.find({"tutorId":tutorId,"tuteeId":tuteeId});
    if (ret.length != 0){
        throw new AlreadyExistException("이미 채팅방이 존재합니다");
    }

    await room.create({
        "tutorId" : tutorId,
        "tuteeId" : tuteeId,
        "messages" : []
    });
    
};
// 채팅을 확인하는 로직
module.exports.checkChats = async (userId, roomId,isTutor) =>{
    const existingRoom = await room.findById(roomId);
    if (!existingRoom){
        throw new InvalidAccessException("채팅방이 존재하지 않습니다");
    }
    if (existingRoom.tutorId != userId && existingRoom.tuteeId != userId){
        throw new InvalidAccessException("비정상적인 접근 입니다");
    }

    existingRoom.messages.forEach(ele=>{
        // 내가 보낸게 아니고 읽지 않은 메세지
        if(ele.senderId != userId && ele.isRead ===false){
            ele.isRead = true;
        }
    });

    await existingRoom.save();




};

// 채팅을 작성하는 로직
module.exports.createChat = async (senderId, receiverId, text, isTutor,socket) => {
    let tutorId, tuteeId;
    const createdAt = new Date();


    if (isTutor) {
        tutorId = senderId;
        tuteeId = receiverId;
    } else {
        tutorId = receiverId;  
        tuteeId = senderId;
    }

    // 채팅방 검색
    const existingRoom = await room.findOne({ "tutorId": tutorId, "tuteeId": tuteeId });

    if (!existingRoom) {
        throw new InvalidAccessException("채팅방이 존재하지 않습니다");
    }

    

    // 메세지 생성
    const newMessage = {
        isRead : false,
        content: text,
        createdAt: createdAt,
        senderId: senderId,
    };

    // 채팅방에 메세지 추가
    existingRoom.messages.push(newMessage);
    

    // 채팅방 저장
    await existingRoom.save();

    const savedMessage = existingRoom.messages[existingRoom.messages.length - 1];

    return {
        // message.createdAt,message.roomId
        "roomId" :  existingRoom._id,
        "createdAt" : createdAt,
        "chatId" : savedMessage._id
    }; 

};

// 채팅 소켓 전송 로직
module.exports.sendChatToSocket = async (userId,senderId,createdAt,roomId,text,socket,jwt,chatId) =>{
    let sender,name; 


    if (senderId == userId){
        sender = await fetchUser(jwt);
        name = sender.name;
    }else{
        sender = await fetchUserById(jwt,senderId);
        name = sender.nickName;
    }

    const message = {
        content : text,
        createdAt : dateFormat(createdAt),
        id : chatId,
        sender : 
            {
                name : name,
                profile : sender.profile,
                userId : sender.id
            }
    };
    console.log(`${roomId.toString()}으로 소켓 이벤트 전송`);
    
    socket.to(roomId.toString()).emit("message",message);



};


// 채팅 날짜 포맷팅 함수

const dateFormat  = (dateString) => {
    let date = new Date(dateString);

    const formattedDate = `${date.getFullYear()}:${String(date.getMonth() + 1).padStart(2, '0')}:${String(date.getDate()).padStart(2, '0')}:${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  
    return formattedDate;
  };