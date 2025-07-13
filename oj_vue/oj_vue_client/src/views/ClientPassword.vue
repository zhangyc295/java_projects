<template>
    <div class="user-info-page">
        <div class="oj-user-info oj-user-info-page">
            <div class="oj-user-info-body">
                <el-form :model="userDetailForm" label-position="top" status-icon label-width="100px"
                    class="demo-ruleForm">
                    <div class="oj-user-info-subtitle-box">
                        <div class="oj-user-info-subtitle">
                            <span class="oj-user-info-dot">账户安全设置</span>
                            <div class="opt-box">
                                <el-button class="user-info-back" link type="primary" @click="goBack"> 返回 </el-button>
                            </div>
                        </div>
                    </div>

                    <div class="ku-user-section">
                        <!-- 头像 -->
                        <el-form-item>
                            <div class="photo-image-box">
                                <div class="photo-image">
                                    <div class="left">
                                        <img width="80px" height="80px" v-if="!userDetailForm.headImage"
                                            src="@/assets/user/head_image.png" />
                                        <img width="80px" height="80px" v-else :src="userDetailForm.headImage" />
                                    </div>
                                </div>
                            </div>
                        </el-form-item>

                        <!-- 手机号 -->
                        <el-form-item label="手机号">
                            <span class="right-info">{{ userDetailForm.telephone || '暂未绑定手机号' }}</span>
                        </el-form-item>

                        <!-- 用户名 -->
                        <el-form-item label="用户名">
                            <span class="right-info">{{ userDetailForm.userName || '暂无用户名，请先至个人中心完善信息' }}</span>
                        </el-form-item>

                        <!-- 新密码 -->
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input type="password" v-model="userDetailForm.userPassword" autocomplete="off"
                                placeholder="密码长度必须在6到18位之间"></el-input>
                        </el-form-item>

                        <!-- 确认新密码 -->
                        <el-form-item label="确认新密码" prop="confirmPassword">
                            <el-input type="password" v-model="userDetailForm.confirmPassword" autocomplete="off"
                                placeholder="密码长度必须在6到18位之间"></el-input>
                        </el-form-item>

                        <!-- 验证码 -->
                        <el-form-item label="验证码" prop="verifyCode">
                            <el-input type="text" v-model="userDetailForm.code" autocomplete="off"
                                placeholder="请输入验证码"></el-input>
                        </el-form-item>
                    </div>

                    <!-- 底部按钮 -->
                    <div class="ku-login-footer">
                        <el-button class="ku-login-footer-btn blue" type="primary" plain
                            @click="getCode">{{ txt }}</el-button>
                        <el-button class="ku-login-footer-btn blue" type="primary" plain
                            @click="savePassword">保存</el-button>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>


<script setup>
import { getUserDetailService } from '@/api/user'
// import { getUserDetailService, editUserService, updateHeadImageService } from '@/api/user'
import { getToken } from '@/utils/cookie'

import router from '@/router'
import { ElMessage } from 'element-plus';
import { reactive, ref } from 'vue'
import { sendCodeService,editUserPasswordService } from '@/api/user'

const userDetailForm = reactive({})
const currentState = ref('normal')

async function getUserDetail() {
    const userRef = await getUserDetailService()
    currentState.value = "normal"
    Object.assign(userDetailForm, userRef.data)
    mobileForm.telephone = userDetailForm.telephone
}
getUserDetail()

function goBack() {
    router.go(-1)
}



// 验证码登录表单
let mobileForm = reactive({
    telephone: '',
    code: ''
})
const txt = ref('获取验证码')
let timer = null

// 获取验证码逻辑
async function getCode() {

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

async function savePassword() {
    await editUserPasswordService(userDetailForm)
    try {
        ElMessage.success("密码设置成功")
        router.push('/client-oj/home/question')
    } catch (error) {
        console.error('修改失败', error)
    }
}


</script>

<style lang="scss" scoped>
.back-btn-box {
    background: #fff;
    width: 100%;
    height: 48px;
    margin-bottom: 16px;

    .back-btn {
        height: 100%;
        margin: 0 auto;
        display: flex;
        align-items: center;
        font-size: 14px;
        font-weight: 400;
        color: #999999;
        cursor: pointer;
        line-height: unset;

        i {
            margin-right: 6px;
            font-size: 16px;
            margin-top: 2.5px;
        }
    }
}

.user-info-page {
    background-color: rgba(247, 247, 247, 1);
    position: relative;
    overflow: hidden;

    :deep(.el-form) {
        .el-button--info {
            background: #f2f3f5;
            border: 1px solid #f2f3f5;
            color: #222;

            &:hover {
                background: #32C5FF;
                border: 1px solid #32C5FF;
                color: #fff;
            }
        }

        .el-form-item {
            max-width: 1120px;
            width: 100%;

            .el-form-item__label {
                font-family: PingFangSC, PingFang SC;
                font-weight: 600;
                font-size: 18px;
                color: #222222;
                margin-bottom: 16px;
            }

            .photo-image-box {
                display: flex;
                align-items: center;
                justify-content: space-between;
                width: 100%;
                padding-bottom: 20px;
                border-bottom: 1px solid #ebebeb;

                .photo-image {
                    display: flex;
                    flex: 1;
                    margin-right: 20px;

                    img {
                        border-radius: 50%;
                    }

                    .right {
                        flex: 1;
                        margin-left: 20px;
                        display: flex;
                        flex-direction: column;
                        justify-content: center;

                        .bold {
                            height: 25px;
                            font-family: PingFangSC, PingFang SC;
                            font-weight: 600;
                            font-size: 18px;
                            color: #222222;
                            line-height: 25px;
                            margin-bottom: 10px;
                        }

                        .normal {
                            height: 22px;
                            font-family: PingFangSC, PingFang SC;
                            font-weight: 400;
                            font-size: 16px;
                            color: #999999;
                            line-height: 22px;
                        }
                    }
                }
            }

            .el-input,
            .el-select,
            .el-radio-group {
                width: 100%;
                height: 44px;
                border-radius: 4px;
                outline-style: none;
                border: none;
                font-size: 16px;

                input {
                    color: #222;
                }
            }

            .el-radio-group {

                border-bottom: 1px solid #ebebeb;
            }

            .el-input__wrapper {
                border: none;
                box-shadow: none;
                border-radius: 0;
                padding-left: 0;
                color: #222;
                border-bottom: 1px solid #ebebeb;

                &:hover {
                    border-bottom: 1px solid #32c5ff;
                }
            }
        }
    }

    .oj-user-info {
        margin: 0 auto;
        max-width: 1520px;
        width: 100%;

        &.oj-user-info-page {
            background: #f7f7f7;
            margin-bottom: 30px;

            .user-info-back {
                cursor: pointer;
                color: #999999;
                font-size: 20px;
            }

            .user-info-edit {
                cursor: pointer;
                font-size: 20px;
                margin-left: auto;
                // margin-left: 0 !important; 
            }

            .ku-user-section {
                background: #fff;
            }

            .oj-user-info-body {
                padding: 0;

                .oj-user-info-subtitle-box {
                    padding: 30px 20px;
                    padding-bottom: 0;

                    .oj-user-info-subtitle {
                        display: flex;
                        align-items: center;
                        height: 33px;
                        font-family: PingFangSC, PingFang SC;
                        font-weight: 600;
                        font-size: 24px;
                        color: #222222;
                        line-height: 33px;

                    }
                }
            }

            .ku-user-section {
                padding: 30px 24px;
                padding-bottom: 0;
            }

            .ku-user-real-info {
                margin-top: 0;
                margin-bottom: 0;
                padding-left: 24px;
                box-sizing: border-box;
                height: 60px;
                line-height: 60px;
                border-bottom: 1px solid #f2f2f2;
            }

            .el-form-item__content {
                margin-left: 110px;
            }

            .ku-login-footer {
                text-align: left;
                padding-left: 24px;
                padding-bottom: 24px;
            }
        }

        .oj-user-info-title {
            background-color: rgba(255, 255, 255, 1);
            border-radius: 10px;
            width: 100%;
            /* 设置宽度为100%以确保水平居中 */
            height: 60px;
            font-size: 25px;
            text-indent: 20px;
            display: flex;
            align-items: center;
            margin: 20px 0 20px 0;
        }

        .oj-user-info-body {
            background-color: #fff;
            padding: 20px 20px 40px;
            border-radius: 8px;
        }

        .oj-user-info-subtitle {
            font-size: 16px;
            font-weight: 500;
            color: #222222;
            line-height: 25px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .ku-login-footer {
            text-align: center;
            margin-top: 30px;
        }

        .ku-login-footer-btn {
            width: 130px;
            height: 44px;
            border-radius: 3px;
            font-size: 15px;
            font-weight: 400;

            &.blue {
                background: #32C5FF !important;
                color: #fff;
            }
        }

        .ku-user-section {
            background-color: #f7f7f7;
            padding: 30px 38px;
            border-radius: 10px;
        }

        .oj-user-info-action {
            position: relative;
            height: 0;
        }

        .ku-user-validate-phone {
            display: inline-block;
            height: 40px;
            margin-left: 8px;
            color: #24c68b;
            font-weight: 700;
            cursor: pointer;
            user-select: none;
        }

        .ku-user-real-info {
            margin-top: 47px;
        }
    }
}
</style>

<style lang="scss">
.oj-user-info-page {
    .el-form-item__label {
        padding-right: 30px !important;
        color: #666;
    }

    .el-form {
        padding-bottom: 24px;
    }

    .right-info {
        display: inline-block;
        min-width: 90px;
        color: #222;
        font-family: PingFangSC, PingFang SC;
        font-weight: 400;
        font-size: 16px;
        width: 100%;
        border-bottom: 1px solid #f2f2f2;
        height: 44px;
        line-height: 44px;

        &.gray {
            color: #999;
        }
    }
}
</style>