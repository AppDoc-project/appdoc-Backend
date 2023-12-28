const InvalidAccessException = require("../exceptions/InvalidAcessException");
const {fetchUserById,fetchUser} = require("./UserService");
const room = require("../schemas/room");
const AlreadyExistException = require("../exceptions/AlreadyExistException");


const mapToRoomResponse = async (arr, isTutor, jwt) => {
    const response = [];
  
    for (let i = 0; i < arr.length; i++) {
      const { tuteeId, tutorId, totalMessageCount, tutorReadMessageCount, tuteeReadMessageCount, messages, _id } = arr[i];
      const userId = isTutor ? tuteeId : tutorId;
      const readCount = isTutor ? tutorReadMessageCount : tuteeReadMessageCount;
  
      const ret = await fetchUserById(jwt, userId);
  
      const temp = {
        id: _id,
        target: {
          "name": ret.nickName,
          "userId": ret.id,
          "profile": ret.profile
        },
        notReadYet: totalMessageCount - readCount,
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
  
    return response;
  };

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
  

    return response;

  };

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
        "totalMessageCount" : 0,
        "tutorReadMessageCount" : 0,
        "tuteeReadMessageCount" : 0,
        "messages" : []
    });
    
};

// 채팅을 작성하는 로직
module.exports.createChat = async (senderId, receiverId, text, isTutor) => {
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
        content: text,
        createdAt: createdAt,
        senderId: senderId,
    };

    // 채팅방에 메세지 추가
    existingRoom.messages.push(newMessage);
    existingRoom.totalMessageCount++;  // 전체 메세지 수 증가

    if (isTutor){
        existingRoom.tutorReadMessageCount++;
    }else{
        existingRoom.tuteeReadMessageCount++;
    }

    // 채팅방 저장
    await existingRoom.save();

    return {
        // message.createdAt,message.roomId
        "roomId" :  existingRoom._id,
        "createdAt" : createdAt
    }; 

};

// 채팅 소켓 전송 로직
module.exports.sendChatToSocket = async (userId,senderId,createdAt,roomId,text,socket,jwt) =>{
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
        createdAt : createdAt,
        sender : 
            {
                name : name,
                profile : sender.profile,
                userId : sender.id
            }
    };
    console.log(roomId.toString());

    socket.to(roomId.toString()).emit("message",message);

};