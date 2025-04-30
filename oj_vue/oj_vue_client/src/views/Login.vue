<template>
    <div class="login-page">
        <div class="orange"> </div>
        <div class="blue"></div>
        <div class="blue small"></div>
        <div class="purple"></div>
        <div class="green"></div>
        <div class="pink"></div>

        <div class="login-box">
            <div class="logo-box">
                <img src="@/assets/logo.png">
                <div>
                    <div class="sys-name">在线OJ平台</div>
                    <div class="sys-sub-name">
                        <span>智能判题</span>
                        <span>高效学习</span>
                    </div>
                </div>
            </div>
            <!-- <div class="form-box-title">
                <span>验证码登录</span>
            </div> -->
            <div class="form-box-title">
                <span :class="{ active: loginMode === 'code' }" @click="loginMode = 'code'">
                    验证码登录
                </span>
                <span :class="{ active: loginMode === 'account' }" @click="loginMode = 'account'">
                    账号密码登录
                </span>
            </div>
            <!-- <div class="form-box">
                <div class="form-item">
                    <img src="@/assets/images/phone.png">
                    <el-input v-model="mobileForm.telephone" type="text" placeholder="请输入手机号" />
                </div>
                <div class="form-item">
                    <img src="@/assets/images/code.png">
                    <el-input style="width:134px" v-model="mobileForm.code" type="text" placeholder="请输入验证码" />
                    <div class="code-btn-box" @click="getCode">
                        <span>{{ txt }}</span>
                    </div>
                </div>
                <div class="submit-box" @click="loginFun">
                    登录/注册
                </div>
            </div> -->
            <div class="form-box">
                <!-- 验证码登录 -->
                <template v-if="loginMode === 'code'">
                    <div class="form-item">
                        <img src="@/assets/images/phone.png">
                        <el-input v-model="mobileForm.telephone" placeholder="请输入手机号" />
                    </div>
                    <div class="form-item">
                        <img src="@/assets/images/code.png">
                        <el-input v-model="mobileForm.code" placeholder="请输入验证码" style="width:134px" />
                        <div class="code-btn-box" @click="getCode">
                            <span>{{ txt }}</span>
                        </div>
                    </div>
                </template>

                <!-- 账号密码登录 -->
                <template v-else>
                    <div class="form-item">
                        <img src="@/assets/images/phone.png">
                        <el-input v-model="accountForm.userName" placeholder="请输入账户名 (第一次登录请使用验证码登录)" />
                    </div>
                    <div class="form-item">
                        <img src="@/assets/images/code.png">
                        <el-input v-model="accountForm.userPassword" type="password" placeholder="请输入密码" />
                    </div>
                </template>

                <div class="submit-box" @click="loginFun">
                    {{ loginMode === 'code' ? '验证码注册/登录' : '账号密码登录' }}
                </div>
            </div>

            <div class="gray-bot">
                <p>注册或点击登录代表您同意 <span>服务条款</span> 和 <span>隐私协议</span></p>
            </div>
        </div>
    </div>
</template>
<!-- <script setup>
import { ref, reactive } from 'vue'
import { setToken } from '@/utils/cookie'
import { sendCodeService, codeLoginService } from '@/api/user'
import router from '@/router'



// 验证码登录表单
let mobileForm = reactive({
    telephone: '',
    code: ''
})
let txt = ref('获取验证码')
let timer = null
async function getCode() {
    await sendCodeService(mobileForm)
    txt.value = '59s'
    let num = 59
    timer = setInterval(() => {
        num--
        if (num < 1) {
            txt.value = '重新获取验证码'
            clearInterval(timer)
        } else {
            txt.value = num + 's'
        }
    }, 1000)
}

async function loginFun() {
    const loginRef = await codeLoginService(mobileForm)
    setToken(loginRef.data)
    router.push('/client-oj/home')

}
</script> -->

<script setup>
import { ref, reactive } from 'vue'
import { setToken } from '@/utils/cookie'
import { sendCodeService, codeLoginService, accountLoginService } from '@/api/user' // 👈 注意添加账号登录接口
import router from '@/router'

// 登录方式：'code' 或 'account'
const loginMode = ref('code')

// 验证码登录表单
let mobileForm = reactive({
    telephone: '',
    code: ''
})

// 账号密码登录表单
let accountForm = reactive({
    userName: '',
    userPassword: ''
})

// 获取验证码按钮文字和定时器
let txt = ref('获取验证码')
let timer = null

// 获取验证码逻辑
async function getCode() {
    if (!mobileForm.telephone) {
        txt.value = '请输入手机号'
        return
    }

    try {
        await sendCodeService(mobileForm)
        txt.value = '59s'
        let num = 59
        timer = setInterval(() => {
            num--
            if (num < 1) {
                txt.value = '重新获取验证码'
                clearInterval(timer)
            } else {
                txt.value = num + 's'
            }
        }, 1000)
    } catch (e) {
        txt.value = '获取失败，请重试'
        clearInterval(timer)
    }
}

// 登录逻辑，根据 loginMode 决定调用哪个接口
async function loginFun() {
    try {
        let loginRef
        if (loginMode.value === 'code') {
            loginRef = await codeLoginService(mobileForm)
        } else {
            loginRef = await accountLoginService(accountForm)
        }
        setToken(loginRef.data)
        router.push('/client-oj/home/question')
    } catch (error) {
        console.error('登录失败', error)
    }
}


</script>



<style lang="scss" scoped>
.login-page {
    width: 100vw;
    height: 100vh;
    position: relative;
    margin-top: -60px;
    margin-left: -20px;
    overflow: hidden;

    .login-box {
        width: 600px;
        height: 604px;
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
        overflow: hidden;

        .logo-box {
            display: flex;
            align-items: center;

            &.refister-logo {
                margin-bottom: 56px;
            }

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

        .form-box-title {
            height: 116px;
            display: flex;
            align-items: center;

            span {
                font-family: PingFangSC, PingFang SC;
                font-weight: 400;
                font-size: 24px;
                color: #000000;
                line-height: 33px;
                display: block;
                height: 33px;
                margin-right: 40px;
                position: relative;
                letter-spacing: 1px;
                cursor: pointer;

                &.active {
                    font-weight: bold;

                    &::before {
                        position: absolute;
                        content: '';
                        bottom: -13px;
                        left: 0;
                        width: 100%;
                        height: 5px;
                        background: #32C5FF;
                        border-radius: 10px;
                    }
                }
            }
        }

        .gray-bot {
            position: absolute;
            left: 0;
            text-align: center;
            margin-top: 56px;
            width: 100%;
            height: 50px;
            background: #FAFAFA;
            font-family: PingFangSC, PingFang SC;
            font-weight: 400;
            font-size: 14px;
            color: #666666;
            line-height: 50px;

            p {
                margin: 0;
            }

            span {
                color: #32C5FF;
                cursor: pointer;
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
                font-size: 16px;
                color: #FFFFFF;
                letter-spacing: 1px;

                &.refister-submit {
                    margin-top: 72px;
                }

                &:hover {
                    background: #0bb4f7;
                }
            }

            .form-item {
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
                    margin-left: 20px;
                    /* 通过调整这个值来添加左侧的间距 */
                    cursor: pointer;

                    &:hover {
                        background: #0bb4f7;
                    }

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