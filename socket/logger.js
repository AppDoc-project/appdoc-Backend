const { createLogger, format, transports } = require("winston");

const logger = createLogger({
    level: 'info',
    format: format.json(),
    transports: [
        new transports.File({
            filename: "/Users/woo/src/app/socket/logs/socket_log", // 로그 파일 경로 수정
        }),
        new transports.File({
            filename: "/Users/woo/src/app/socket/logs/socket_error",
            level: 'error',
        }),
    ],
});

module.exports = logger;
