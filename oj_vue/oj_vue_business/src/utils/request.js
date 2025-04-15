import axios from "axios";
import { getToken, removeToken } from "./cookie";
import { ElMessage } from "element-plus";

axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8"


//127.0.0.1:19090
const service = axios.create({
    // baseURL: "http://127.0.0.1:19090/system",
    baseURL: "/dev-api", // 代理服务器 config.js
    timeout: 5000   //5s
})

// Access to XMLHttpRequest at 'http://127.0.0.1:19090/system/admin/login' 
// from origin 'http://localhost:5173' has been blocked by CORS policy:
// 浏览器的 CORS（跨域资源共享）

// 响应拦截器
service.interceptors.response.use(
    (res) => {
        // 未设置状态码则默认成功状态
        const code = res.data.code;
        const msg = res.data.msg;
        if (code === 3001) {//token过期
            ElMessage.error(msg)
            removeToken()
            router.push('/oj/login')
            return Promise.reject(new Error(msg));
        }else if (code !== 1000) {
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


// 请求拦截器
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

export default service

