import axios from 'axios'

const api = axios.create({
  baseURL: 'https://radical-mounts-facial-singing.trycloudflare.com/api',
  timeout: 5000,
})

export default api
