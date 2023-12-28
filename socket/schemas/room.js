const mongoose = require("mongoose");

const { Schema } = mongoose;


const messageSchema = new Schema({
    content: {
        type: String,
        required: true,
    },
    createdAt: {
        type: Date,
        default: Date.now,
    },
    senderId: {
        type: Number,
        required: true,
    }
});


const roomSchema = new Schema({
    tuteeId: {
        type: Number,
        required: true,
    },
    tutorId: {
        type: Number,
        required: true,
    },
    totalMessageCount: {
        type: Number,
        required: true,
    },
    tuteeReadMessageCount: {
        type: Number,
        required: true,
    },
    tutorReadMessageCount: {
        type: Number,
        required: true,
    },
    messages: [messageSchema],
});

// tuteeId와 tutorId의 조합이 unique해야 함
roomSchema.index({ tuteeId: 1, tutorId: 1 }, { unique: true });

// Room 모델을 생성하여 내보냄
module.exports = mongoose.model('Room', roomSchema);
