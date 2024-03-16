// require('pinpoint-node-agent');
const express = require("express");
const morgan = require("morgan");
require('dotenv').config({ path: `.env.${process.env.NODE_ENV || 'local'}` });
const {devInit} = require("./devInit/devInit");
const app = express();
const lesson = require("./schemas/lesson");
const connect = require("./schemas/index");
const port = process.env.PORT;
const {socketConfig} = require("./socket.js");
const logger = require("./logger");
const rabbitFunction = require("./service/RabbitService");

connect();

app.use(morgan("dev"));
app.use(express.urlencoded({ extended: true }));
app.use(express.json());
// 초기 설정


// 채팅용 컨트롤러
const chatController = require("./controller/ChatController");
app.use("/chat",chatController);


// 404처리 
const nonMatchFilter = require("./controller/filters/NonMatchFilter");
app.use(nonMatchFilter);

// 에러처리
const exceptionHandler = require("./controller/filters/ExceptionHandler");
app.use(exceptionHandler);


const server = app.listen(port, () => {
  console.log(`Server Port: ${port}`);
  });
// 서버 오픈

// 소켓 설정
socketConfig(server,app);

// rabbit MQ 설정
rabbitFunction(app);