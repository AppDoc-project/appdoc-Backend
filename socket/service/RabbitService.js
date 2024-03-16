const amqp = require('amqplib');
const Lesson = require("../schemas/lesson");
const queueName = 'queue';
/*
* rabbit mq 관련 서비스 제공
* 레슨 서비스로부터 메세지를 받아서 레슨을 개설한다
*/ 
module.exports =  async (app) => {
    try {
        const io = app.get("io");
        const lessonSocket = io.of("/chat/lesson");
        const connection = await amqp.connect(`amqp://${process.env.rabbit}`);
        const channel = await connection.createChannel();
        await channel.assertQueue(queueName);

        channel.consume(queueName, async(msg) => {
            const message = JSON.parse(msg.content.toString());
            if (message.type==='open'){
                await Lesson.create({"lessonId":message.lessonId,"tuteeId":message.tuteeId,"tutorId":message.tutorId});
            }else{
                lessonSocket.to(message.lessonId.toString()).emit("disconnect_event",message.message);
                console.log("채팅방 떠남")
                console.dir(message);
            }

        }, { noAck: true });
        


    } catch (error) {
        console.error(error);
    }
}

