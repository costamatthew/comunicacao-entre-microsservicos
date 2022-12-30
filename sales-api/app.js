import express from 'express';

import { connect } from './src/config/db/mongoDbConfig.js';
import { createInitialOrder } from './src/config/db/initialData.js';

const app = express();
const env = process.env;
const PORT = env.PORT || 8082;

connect();
createInitialOrder();

app.get('/api/status', async (req, res) => {
  return res.status(200).json({
    serice: 'Sales-API',
    status: 'up',
    httpStatus: 200,
  });
});

app.listen(PORT, () => {
  console.info(`Server started successfully at port ${PORT}`);
});
