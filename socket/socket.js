const socketIo = require("socket.io");
const { fetchUser } = require("./service/UserService");
const Room = require("./schemas/room");
const querystring = require("querystring");

const queryParse = (req) => {
  const { url } = req;
  const queryString = url.split("?")[1];

  if (queryString) {
    const parsedQuery = querystring.parse(queryString);
    req.query = parsedQuery;
  }
};

module.exports = async (server,app)=>{

    const io = socketIo(server,{path:'/socket.io',  cors: {
        origin: "*",
        methods: ["GET", "POST"]
      }});

      app.set("io",io);
      const room = io.of("/room");
      room.on("connection", async(socket)=>{
        const req = socket.request;
        queryParse(req);
        await socketUser(req);
        const {roomId} = req.query;
        const chatRoom = await Room.findById(roomId);
        if (chatRoom.tuteeId != req.user.id &&  chatRoom.tutorId != req.user.id){
            return;
        }

        socket.join(roomId);
        
      });

      console.log("소켓 설정 완료");
};

const socketUser = async (req) => {
    ret = await fetchUser(req.headers.authorization);
    req.user = ret;
};