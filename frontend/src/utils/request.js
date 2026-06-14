import axios from 'axios';

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 15000,
});

request.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

request.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'));
    }
    return res;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default request;
