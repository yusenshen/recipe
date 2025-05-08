import axios from 'axios'

const api = axios.create({
  baseURL: 'https://nation-developed-collect-dimensions.trycloudflare.com/api',
  timeout: 5000,
})

export default api
