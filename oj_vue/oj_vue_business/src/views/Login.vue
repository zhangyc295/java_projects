<template>
    <div class="login-page">
        <div class="orange"> </div>
        <div class="blue"></div>
        <div class="blue small"></div>
        <!-- 新增的三个元素 -->
        <div class="purple"></div>
        <div class="green"></div>
        <div class="pink"></div>

        <div class="login-box">
            <div class="logo-box">
                <div class="right">
                    <div class="sys-name">在线OJ平台管理端</div>
                    <div class="sys-sub-name">
                        <span>智能判题</span>
                        <span>高效管理</span>
                    </div>
                </div>
            </div>
            <div class="form-box">
                <div class="form-item">
                    <img src="../assets/images/admin.png">
                    <el-input v-model="account" placeholder="请输⼊账号" />
                </div>
                <div class="form-item">
                    <img src="../assets/images/password.png">
                    <el-input v-model="password" type="password" show-password placeholder="请输⼊密码" />
                </div>
                <div class="submit-box" @click="login()">
                    登录
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import router from '@/router'
import { adminLogin } from '@/api/admin'
import { setToken } from '@/utils/cookie'
import { ElMessage } from 'element-plus'

// const input = ref('测试内容') // 默认值测试
const account = ref('')
const password = ref('')

// setTimeout(() =>{
//   console.log(input.value)
// },5000)
// 延时执行
async function login() {
    try {
        //通过request.js的响应拦截器
        const loginResult = await adminLogin(account.value, password.value)
        //console.log("loginResult:", loginResult)
        ElMessage.success('登录成功')
        router.push("/oj/system")
        //页面跳转

        setToken(loginResult.data)
        //token储存
    }catch (error){
        console.log("error:", error)
    }
    // if (loginResult.data.code === 1000) {
    //     console.log("登录成功")
    //     //登录成功
    //     router.push("/oj/system")
    //     //页面跳转
    //     setToken(loginResult.data.data)
    //     //token储存
    // } else {
    //     console.log("登录失败")
    //     //登录失败
    //     ElMessage.error(loginResult.data.msg)
    // }
}
</script>

<style lang="scss" scoped>
.login-page {
    width: 100vw;
    height: 100vh;
    position: relative;
    overflow: hidden;

    .login-box {
        overflow: hidden;

        .logo-box {
            display: flex;
            align-items: center;
            margin-bottom: 30px;

            img {
                width: 68px;
                height: 68px;
                margin-right: 16px;
            }

            .sys-name {
                height: 33px;
                font-family: PingFangSC, PingFang SC;
                font-weight: 600;
                font-size: 24px;
                color: #222222;
                line-height: 33px;
                margin-bottom: 13px;
            }

            .sys-sub-name {

                height: 22px;
                font-family: PingFangSC, PingFang SC;
                font-weight: 400;
                font-size: 16px;
                color: #222222;
                line-height: 22px;
            }

            .sys-sub-name span:first-child {
                margin-right: 1.5em;
                /* 调整这个值控制间距 */
            }
        }

        :deep(.form-box) {
            .submit-box {
                margin-top: 90px;
                width: 456px;
                height: 48px;
                background: #32C5FF;
                border-radius: 8px;
                cursor: pointer;
                display: flex;
                justify-content: center;
                align-items: center;
                font-family: PingFangSC, PingFang SC;
                font-weight: 600;
                font-size: 18px;
                color: #FFFFFF;
                letter-spacing: 1px;
            }

            .form-item {
                // background: #FFFFFF; /* 改为纯白背景 */


                display: flex;
                align-items: center;
                width: 456px;
                height: 48px;
                background: #F8F8F8;
                border-radius: 8px;
                margin-bottom: 30px;
                position: relative;

                .code-btn-box {
                    position: absolute;
                    right: 0;
                    width: 151px;
                    height: 48px;
                    background: #32C5FF;
                    border-radius: 8px;
                    top: 0;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;

                    span {
                        font-family: PingFangSC, PingFang SC;
                        font-weight: 400;
                        font-size: 16px;
                        color: #FFFFFF;
                    }
                }

                .error-tip {
                    position: absolute;
                    width: 140px;
                    text-align: right;
                    padding-right: 12px;
                    height: 20px;
                    font-family: PingFangSC, PingFang SC;
                    font-weight: 400;
                    font-size: 14px;
                    color: #FD4C40;
                    line-height: 20px;
                    right: 0;

                    &.bottom {
                        right: 157px;
                    }
                }

                .el-input {
                    width: 380px;
                    font-family: PingFangSC, PingFang SC;
                    font-weight: 400;
                    font-size: 16px;
                    color: #222222;
                }

                .el-input__wrapper {
                    border: none;
                    box-shadow: none;
                    background: transparent;
                    width: 230px;
                    padding-left: 0;
                }

                img {
                    width: 24px;
                    height: 24px;
                    margin: 0 18px;
                }
            }
        }

        width:456px;
        height:404px;
        background: #FFFFFF;
        box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.1);
        border-radius: 10px;
        opacity: 0.9;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 2;
        padding: 0 72px;
        padding-top: 50px;
    }

    &::after {
        position: absolute;
        top: 0;
        left: 0;
        height: 100vh;
        bottom: 0;
        right: 0;
        background: rgba(255, 255, 255, .8);
        z-index: 1;
        content: '';
    }

    .orange {
        background: #F0714A;
        width: 498px;
        height: 498px;
        border-radius: 50%;
        background: #F0714A;
        opacity: 0.67;
        filter: blur(50px);
        left: 14.2%;
        top: 41%;
        position: absolute;
    }

    .blue {
        width: 334px;
        height: 334px;
        background: #32C5FF;
        opacity: 0.67;
        filter: blur(50px);
        left: 14.2%;
        top: 42%;
        position: absolute;
        top: 16.3%;
        left: 80.7%;

        &.small {
            width: 186px;
            height: 186px;
            top: 8.2%;
            left: 58.2%;
        }
    }

    /* 新增粉色元素 */
    .pink {
        background: #FF6B8B;
        width: 350px;
        height: 350px;
        border-radius: 50%;
        opacity: 0.55;
        filter: blur(50px);
        position: absolute;
        bottom: 10%;
        right: 15%;
        z-index: 0;
    }

    /* 新增紫色元素 */
    .purple {
        background: #9B59B6;
        width: 420px;
        height: 420px;
        border-radius: 50%;
        opacity: 0.6;
        filter: blur(50px);
        position: absolute;
        top: 5%;
        left: 8%;
        z-index: 0;
    }

    /* 新增绿色元素 */
    .green {
        background: #2ECC71;
        width: 280px;
        height: 280px;
        border-radius: 50%;
        opacity: 0.5;
        filter: blur(50px);
        position: absolute;
        top: 20%;
        left: 65%;
        z-index: 0;
    }
}
</style>