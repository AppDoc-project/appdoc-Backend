const mongoose = require("mongoose");

const { Schema } = mongoose;

// 레슨 스키마 정의
const lessonSchema = new Schema({
    lessonId:{
        type: Number,
        required: true
    },
    tutorId: {
        type: Number,
        required: true,
    },
    tuteeId:{
        type: Number,
        required: true
    }
    
});




lessonSchema.index({lessonId:1}, { unique: true });

// Lesson 모델을 생성하여 내보냄
module.exports = mongoose.model('Lesson', lessonSchema);
