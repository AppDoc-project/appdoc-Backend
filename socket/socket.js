const socketIo = require("socket.io");
const { fetchUser } = require("./service/UserService");
const Room = require("./schemas/room");
const Lesson = require("./schemas/lesson");
const querystring = require("querystring");

// 소켓 관련 설정


const socketConfig = async (server,app)=>{

    const io = socketIo(server,{path:'/socket.io',  cors: {
        origin: "*",
        methods: ["GET", "POST"]
      }});

  

      app.set("io",io);
      const room = io.of("/chat/socket");
      const roomOuter = io.of("/chat/outer");
      const lesson = io.of("/chat/lesson");


      // 채팅 방 내부 소켓
      room.on("connection", async(socket)=>{
        const req = socket.request;
        try{
          queryParse(req);
          await socketUser(req);
          const {roomId} = req.query;
          const chatRoom = await Room.findById(roomId);
          if (chatRoom.tuteeId != req.user.id &&  chatRoom.tutorId != req.user.id){
              return;
          }
          console.log(`${req.user.nickName}님 채팅방 입장`);
          socket.join(roomId);
        }catch(err){
          console.error(err);
        }

      });

      // 채팅 방 외부 소켓
      roomOuter.on("connection", async(socket)=>{
        try{
          const req = socket.request;
          queryParse(req);
          await socketUser(req);
          socket.join(req.user.id);
        }catch(err){
          console.error(err);
        }
        

      });

      // 화상 채팅 소켓
      lesson.on("connection",async(socket)=>{
        try{
          const req = socket.request;
          queryParse(req);
          const {lessonId} = req.query;
          console.log(lessonId);
          await socketUser(req);

          socket.join(lessonId);
          console.log(`${req.user.nickName}님 화상채팅 입장`);

          socket.on('disconnect', () => {
            socket.emit("disconnect_event","상대방이 방을 나갔습니다. 통화를 종료합니다.");
            console.log(`${req.user.nickName}님 화상채팅 퇴장`);
        });
            
        
 


        }catch(err){
          console.error(err);
        }
      });





      console.log("소켓 설정 완료");
};

const queryParse = (req) => {
  const { url } = req;
  const queryString = url.split("?")[1];

  if (queryString) {
    const parsedQuery = querystring.parse(queryString);
    req.query = parsedQuery;
  }
};

const socketUser = async (req) => {
    ret = await fetchUser(req.headers.authorization);
    req.user = ret;
};

module.exports.socketConfig = socketConfig;


