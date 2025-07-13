import axios from 'axios'
import { getToken, removeToken } from './cookie'
import router from '@/router'; // 必须导入

//不同的功能，通过axios请求的是不同接口的地址
//127.0.0.1:19090
const service = axios.create({
  baseURL:"/dev-api",
  timeout:5000,
})
axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8";

//请求拦截器
service.interceptors.request.use(
  (config) => {
    if (getToken()) {
      config.headers["Authorization"] = "Bearer " + getToken();
    }
    return config;
  },
  (error) => {
    console.log(error)
    Promise.reject(error);
  }
);

//响应拦截器
service.interceptors.response.use(
  (res) => {  //res : 响应数据
    // 未设置状态码则默认成功状态
    const code = res.data.code;
    const msg = res.data.msg;
    if (code === 3001) {
      ElMessage.error(msg);
      removeToken()
      return Promise.reject(new Error(msg));
    } else if (code !== 1000) {
      ElMessage.error(msg);
      return Promise.reject(new Error(msg));
    } else {
      return Promise.resolve(res.data);
    }
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default service

//http://localhost:5173/dev-api/sysUser/login

//同源策略

// 同源策略指的就是，浏览器出于安全考虑，实施的一种策略，即只允许来自同一源（即协议+域名+端口都相同）的请求访问资源。否则就会导致致跨域问题。

// 例如 http://xxxx.com -> https://xxxx.com 存在跨域 协议不同   http://xxxx.com -> http://xxxx.com

// 例如 127.x.x.x:8001 -> 127.x.x.x:8002 存在跨域 端口不同

// 例如 www.xxxx.com -> www.yyyy.com 存在跨域 域名不同

//前端（浏览器） ---》  代理服务器  ——》  后端