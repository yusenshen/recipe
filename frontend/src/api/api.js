import axios from 'axios'

const api = axios.create({
  baseURL: 'http://d025-116-15-88-34.ngrok-free.app/api',
  timeout: 5000,
})

export default api
