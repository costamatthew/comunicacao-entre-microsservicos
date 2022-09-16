import express from 'express'
import { createInitialData } from './src/config/db/initialData.js'

const app = express()
const env = process.env
const PORT = env.PORT || 8080

await createInitialData()

app.get('/api/status', (req, res) => {
  return res.status(200).json({
    serice: 'Auth-API',
    status: 'up',
    httpStatus: 200,
  })
})

app.listen(PORT, () => {
  console.info(`Server started successfully at port ${PORT}`)
})
