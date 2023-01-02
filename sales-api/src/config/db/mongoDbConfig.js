import mongoose from 'mongoose';

import { MONGO_DB_URL } from '../constants/secrets.js';

export function connectMongoDB() {
  mongoose.connect(MONGO_DB_URL, {
    useNewUrlParser: true,
  });
  mongoose.connection.on('connection', function () {
    console.info('The application connected to MongoDB successfully');
  });
  mongoose.connection.on('error', function () {
    console.error('error');
  });
}
