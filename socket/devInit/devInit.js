const room = require("../schemas/room");


module.exports.devInit = async (app) => {
    const messages = [];
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now(),
        senderId: 1,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now(),
        senderId: 2,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+60000,
        senderId: 1,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+120000,
        senderId: 2,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+180000,
        senderId: 2,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+240000,
        senderId: 2,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+360000,
        senderId: 1,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+420000,
        senderId: 2,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+480000,
        senderId: 2,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+540000,
        senderId: 1,
    });
    messages.push({
        content: "안녕안년안녕안녕",
        createdAt: Date.now()+600000,
        senderId: 1,
    });
    
    
    await room.create({
        
            "tutorId" : 2,
            "tuteeId" : 1,
            "totalMessageCount" : 11,
            "tutorReadMessageCount" : 6, // 튜터가 보낸 메세지 수
            "tuteeReadMessageCount" : 5, // 튜티가 보낸 메세지 수
            "messages" : messages
        }   
    );

}