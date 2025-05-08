import axios from 'axios'

const api = axios.create({
  baseURL: ' https://827d-116-15-88-34.ngrok-free.app/api',
  timeout: 5000,
})

export default api
