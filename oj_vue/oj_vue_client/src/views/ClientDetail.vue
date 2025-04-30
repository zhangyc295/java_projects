<template>
    <div class="user-info-page">
        <div class="oj-user-info oj-user-info-page">
            <div class="oj-user-info-body">
                <el-form :model="userDetailForm" label-position="top" status-icon label-width="100px"
                    class="demo-ruleForm">
                    <div class="oj-user-info-subtitle-box">
                        <div class="oj-user-info-subtitle">
                            <span class="oj-user-info-dot">
                                基本信息
                            </span>
                            <div class="opt-box">
                                <el-button class="user-info-edit" v-if="isDisabled()" link type="primary"
                                    @click="changeState('updating')"> 编辑
                                </el-button>
                                <el-button class="user-info-edit" v-if="!isDisabled()" link type="primary"
                                    @click="changeState('normal')"> 取消
                                </el-button>
                                <el-button class="user-info-back" link type="primary" @click="goBack()"> 返回
                                </el-button>
                            </div>
                        </div>

                    </div>
                    <div class="ku-user-section">
                        <el-form-item prop="nickName">
                            <div class="photo-image-box">
                                <div class="photo-image">
                                    <div class="left">
                                        <img width="80px" v-if="!userDetailForm.headImage" height="80px"
                                            src="@/assets/user/head_image.png">
                                        <img width="80px" v-else height="80px" :src="userDetailForm.headImage">
                                    </div>
                                    <div class="right" v-if="!userDetailForm.headImage">
                                        <div class="bold">上传你的个人头像</div>
                                        <div class="normal">上传你的个性头像展示你的个人风格</div>
                                    </div>
                                </div>
                                <div v-if="!isDisabled()">
                                    <el-upload :action="uploadUrl" :headers="headers" :on-error="handleUploadError"
                                        :on-success="handleUploadSuccess" :show-file-list="false">
                                        <el-button type="info">{{ userDetailForm.headImage ? '更换头像' : '上传头像'
                                            }}</el-button>
                                        <template #tip> </template>
                                    </el-upload>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item label="昵称" prop="nickName">
                            <el-input v-if="!isDisabled()" type="text" v-model="userDetailForm.nickName"
                                autocomplete="off" placeholder="请填写您的昵称" maxlength="8"></el-input>
                            <span :class="userDetailForm.nickName ? 'right-info' : 'right-info gray'" v-else>
                                {{ userDetailForm.nickName || "请填写您的昵称" }}
                            </span>
                        </el-form-item>

                        <el-form-item label="用户名" prop="userName">
                            <el-input v-if="!isDisabled()" type="text" v-model="userDetailForm.userName"
                                autocomplete="off" placeholder="请填写您的用户名" maxlength="8"></el-input>
                            <span :class="userDetailForm.userName ? 'right-info' : 'right-info gray'" v-else>
                                {{ userDetailForm.userName || "请填写您的用户名" }}
                            </span>
                        </el-form-item>


                        <el-form-item label="性别" prop="gender">
                            <el-radio-group v-model="userDetailForm.gender" v-if="!isDisabled()">
                                <el-radio :value="1">男</el-radio>
                                <el-radio :value="2">女</el-radio>
                            </el-radio-group>
                            <span :class="userDetailForm.gender ? 'right-info' : 'right-info gray'" v-else>
                                {{ userDetailForm.gender == null ? "请选择您的性别" : userDetailForm.gender == 1 ? "男" : "女" }}
                            </span>
                        </el-form-item>
                        <el-form-item label="学校" prop="school">
                            <el-input v-if="!isDisabled()" type="text" v-model="userDetailForm.school"
                                autocomplete="off" placeholder="请填写您的学校名称"></el-input>
                            <span :class="userDetailForm.school ? 'right-info' : 'right-info gray'" v-else>
                                {{ userDetailForm.school || "请填写您的学校名称" }}
                            </span>
                        </el-form-item>
                        <el-form-item label="专业" prop="major">
                            <el-input v-if="!isDisabled()" type="text" v-model="userDetailForm.major" autocomplete="off"
                                placeholder="请填写您的专业名称"></el-input>
                            <span :class="userDetailForm.major ? 'right-info' : 'right-info gray'" v-else>
                                {{ userDetailForm.major || "请填写您的专业名称" }}
                            </span>
                        </el-form-item>
                        <el-form-item label="手机" prop="telephone">
                            <el-input v-if="!isDisabled()" type="text" v-model="userDetailForm.telephone"
                                autocomplete="off" placeholder="请填写常用手机号"></el-input>
                            <span :class="userDetailForm.telephone ? 'right-info' : 'right-info gray'" v-else>
                                {{ userDetailForm.telephone || "请填写常用手机号" }}
                            </span>
                        </el-form-item>
                        <el-form-item label="常用邮箱" prop="email">
                            <el-input v-if="!isDisabled()" type="text" v-model="userDetailForm.email" autocomplete="off"
                                placeholder="请填写常用邮箱"></el-input>
                            <span :class="userDetailForm.email ? 'right-info' : 'right-info gray'" v-else>
                                {{ userDetailForm.email || "请填写常用邮箱" }}
                            </span>
                        </el-form-item>

                        <el-form-item label="个人介绍" prop="introduction">
                            <el-input v-if="!isDisabled()" :rows="5" type="textarea"
                                v-model="userDetailForm.introduction" autocomplete="off" placeholder="请填写个人介绍"
                                maxlength="80" show-word-limit></el-input>
                            <span :class="userDetailForm.introduction ? 'right-info intro' : 'right-info intro gray'"
                                v-else>
                                {{ userDetailForm.introduction || "请用一段话介绍自己,该简介将会在您的个人主页展示" }}
                            </span>
                        </el-form-item>
                    </div>
                    <div class="ku-login-footer" v-if="!isDisabled()">
                        <el-button class="ku-login-footer-btn" type="info" plain
                            @click="changeState('normal')">取消</el-button>
                        <el-button class="ku-login-footer-btn blue" type="primary" plain
                            @click="editUser()">保存</el-button>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { getUserDetailService, editUserService,updateHeadImageService } from '@/api/user'
import { getToken } from '@/utils/cookie'

import router from '@/router'
import { ElMessage } from 'element-plus';
import { reactive, ref } from "vue"

const userDetailForm = reactive({})
const currentState = ref('normal')

async function getUserDetail() {
    const userRef = await getUserDetailService()
    currentState.value = "normal"
    Object.assign(userDetailForm, userRef.data)
}
getUserDetail()

function goBack() {
    router.go(-1)
}

function changeState(state) {
    currentState.value = state
}

function isDisabled() {
    return currentState.value === "normal"
}

async function editUser() {
    await editUserService(userDetailForm)
    // console.log(userDetailForm) 
    getUserDetail()
}
//http://127.0.0.1:19090/friend/file/upload
const uploadUrl = ref("/dev-api/file/upload")

const headers = ref({
    Authorization: "Bearer " + getToken(),
})

async function handleUploadSuccess(res) {
    if (res.code !== 1000) {
        ElMessage.error(res.msg)
    } else {
        const clientUpdateDTO = reactive({
            headImage: res.data.name
        })
        await updateHeadImageService(clientUpdateDTO)
        getUserDetail()
        ElMessage.success("头像上传成功")
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
            width: 84px;
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