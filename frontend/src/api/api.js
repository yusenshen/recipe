import axios from 'axios'

const api = axios.create({
  baseURL: 'http://192.168.1.6:8080/api',
  timeout: 5000,
})

export default api
