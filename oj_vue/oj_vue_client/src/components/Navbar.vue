<template>
    <div class="oj-navbar">
        <div class="oj-navbar-menus">
            <img class="oj-navbar-logo" src="@/assets/logo.png" />
            <el-menu router class="oj-navbar-menu" mode="horizontal">
                <el-menu-item index="/client-oj/home/question">题库</el-menu-item>
                <el-menu-item index="/client-oj/home/contest">竞赛</el-menu-item>
            </el-menu>
        </div>
        <div class="oj-navbar-users">
            <img v-if="isLogin" class="oj-message" @click="goMessage" src="@/assets/message/message.png" />
            <el-dropdown v-if="isLogin">
                <div class="oj-navbar-name">
                    <img class="oj-head-image" v-if="isLogin" :src="userInfo.headImage" />
                    <span>{{ userInfo.nickName }}</span>
                    <el-icon style="margin-left: 8px">
                        <ArrowDownBold />
                    </el-icon>
                </div>

                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="goUserDetail">
                            <div class="oj-navabar-item">
                                <span>个人中心</span>
                            </div>
                        </el-dropdown-item>
                        <el-dropdown-item @click="setMyPassword">
                            <div class="oj-navabar-item">
                                <span>设置密码</span>
                            </div>
                        </el-dropdown-item>
                        <el-dropdown-item @click="goMyContest">
                            <div class="oj-navabar-item">
                                <span>我的竞赛</span>
                            </div>
                        </el-dropdown-item>
                        <el-dropdown-item>
                            <div class="oj-navabar-item">
                                <span @click="handleLogout">退出登录</span>
                            </div>
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <span class="oj-navbar-login-btn" v-if="!isLogin" @click="goLogin">登录</span>
        </div>
    </div>
</template>

<script setup>
import {
    ArrowDownBold
} from '@element-plus/icons-vue'
import { reactive, ref } from 'vue';
import router from '@/router';
import { getToken, removeToken } from '@/utils/cookie';
import { logoutService, getUserInfoService } from '@/api/user';

const isLogin = ref(false)
const userInfo = reactive({
    nickName: '',
    headImage: ''
})

async function checkLogin() {
    // console.log("Token:", getToken());
    // console.log("请求结果:", await getUserInfoService());
    if (getToken()) {
        //  后端是需要提供一个接口完成，这两件事情的
        //  1. 判断当前token是否过期（判断当前用户是否还处于登录状态）
        //  2. 将当前用户的头像、昵称返回
        const userInfoRes = await getUserInfoService()
        Object.assign(userInfo, userInfoRes.data)

        // console.log(userInfo)
        // console.log(userInfoRes.data)
        isLogin.value = true
    }
}

checkLogin()

function goLogin() {
    router.push('/client-oj/login')
}

function goMyContest() {
    router.push('/client-oj/home/client/contest')
}
function setMyPassword() {
    router.push('/client-oj/home/client/password')
}

function goUserDetail() {
    router.push('/client-oj/home/client/detail')
}

function goMessage() {
    router.push('/client-oj/home/client/message')
}

async function handleLogout() {
    await ElMessageBox.confirm(
        '确认退出',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '退出',
            type: 'warning',
        }
    )
    await logoutService()
    removeToken()
    isLogin.value = false
}


</script>

<style lang="scss" scoped>
.oj-navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    box-sizing: border-box;

    max-width: 1520px;
    margin: 0 auto;

    .oj-navbar-menus {
        display: flex;
        align-items: center;
        height: 50px;

        .el-menu-item {
            font-family: PingFangSC, PingFang SC;
            font-weight: 400;
            font-size: 20px;
            color: #222222;
            line-height: 28px;
            text-align: center;
            width: 42px;
            text-align: left;
            margin-right: 25px;
        }
    }

    .oj-navbar-logo {
        width: 38px;
        height: 38px;
        // background: #ffffff;
        border-radius: 8px;
        cursor: pointer;
        object-fit: contain;
        margin-right: 59px;
    }

    .oj-navbar-menu {
        margin-left: 18 px;
        width: 600px;
        border: none;

        .el-menu-item {
            font-size: 16px;
            font-weight: 500;
            background-color: transparent !important;
            transition: none;
            border: none;
            line-height: 60px;
        }
    }

    .oj-navbar-users {
        display: flex;
        align-items: center;
        margin-right: 40px; // 减少这个数值可以让整体左移
    }

    .oj-navbar-login-btn {
        line-height: 60px;
        display: inline-block;
        font-family: PingFangSC, PingFang SC;
        font-weight: 400;
        font-size: 18px;
        color: #222222;
        text-align: center;
        cursor: pointer;

        .line {
            display: inline-block;
            width: 25px;
        }
    }

    .oj-message {
        cursor: pointer;
        margin-top: 15px;
    }

    .oj-head-image {
        width: 30px;
        height: 30px;
        border-radius: 30px;
        margin-right: 10px;
    }

    .oj-navbar-name {
        cursor: pointer;
        margin-top: 15px;
        font-weight: 400;
        color: #000;
        margin-left: 15px;
        font-size: 20px;
        width: 100px;
        display: flex;
        align-items: center;
    }

    .oj-navabar-item {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 0 32px;
    }

    // 新增：下拉菜单样式优化
    .el-dropdown-menu {
        border: none;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

        .el-dropdown-menu__item {
            padding: 0;

            .oj-navabar-item {
                padding: 0 20px;
                width: 100%;

                &:hover {
                    color: #32C5FF;
                    background: #f5f5f5;
                }
            }
        }
    }
}

// 新增：移除所有焦点轮廓
*:focus {
    outline: none;
}
</style>