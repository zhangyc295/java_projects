<template>
    <el-container class="layout-container">
        <el-header class="el-header" style="display: flex; justify-content: space-between; align-items: center;">
            <div style="font-weight: bold;">在线OJ平台管理端</div>
            <el-dropdown placement="bottom-end">
                <span class="el-dropdown__box">
                    <div><strong>当前⽤⼾：</strong>{{ loginAdmin.nickName }}</div>
                    <el-icon>
                        <ArrowDownBold />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="logout()" :icon="SwitchButton">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </el-header>
        <el-main class="layout-bottom-box">
            <div class="left">
                <el-aside width="200px" class="el-aside">
                    <el-menu class="el-menu" router>
                        <el-menu-item index="/oj/system/user">
                            <el-icon>
                                <User />
                            </el-icon>
                            <span>用户管理</span>
                        </el-menu-item>
                        <el-menu-item index="/oj/system/question">
                            <el-icon>
                                <Memo />
                            </el-icon>
                            <span>题目管理</span>
                        </el-menu-item>
                        <el-menu-item index="/oj/system/contest">
                            <el-icon>
                                <EditPen />
                            </el-icon>
                            <span>竞赛管理</span>
                        </el-menu-item>
                    </el-menu>
                </el-aside>
            </div>
            <div class="right">
                <RouterView />
            </div>
        </el-main>
    </el-container>
</template>

<script setup>
import {
    ArrowDownBold,
    SwitchButton,
    User,
    Memo,
    EditPen
} from '@element-plus/icons-vue'

import { reactive } from 'vue';
import { adminLogout } from '@/api/admin';
import { getInfo } from '@/api/admin';
import { removeToken } from '@/utils/cookie';
import router from '@/router';

const loginAdmin = reactive({
    nickName: ''
})

async function getAdminInfo() {
    const adminInfo = await getInfo()
    loginAdmin.nickName = adminInfo.data.nickName
}
getAdminInfo()

async function logout() {
    await ElMessageBox.confirm(
        '确认退出？',
        '温馨提示',
        {
            confirmButtonText: "确认",
            cancelButtonText: '取消',
            type: 'warning'
        }
    )
        .then(() => {
            ElMessage({
                type: 'success',
                message: '退出成功',
            })
        })
    await adminLogout()
    removeToken()

    router.push("/oj/login"); // 等路由跳转完成后
}

</script>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;
    background: #f7f7f7;
    font-family: "PingFang SC", sans-serif;

    .layout-bottom-box {
        display: flex;
        justify-content: space-between;
        height: calc(100vh - 100px);
        overflow: hidden;

        .left {
            margin-right: 20px;
            background: #fff;
            display: flex;

            :deep(.el-menu) {
                flex: 1;

                .el-menu-item {

                    height: 48px;
                    font-size: 18px; // 基础字号
                    color: #606266; // 默认文字色
                    transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);

                    // 图标样式
                    .el-icon {
                        font-size: 20px; // 图标比文字大2px
                        margin-right: 8px;
                        color: inherit; // 继承文字颜色
                    }

                    // 悬停状态
                    &:not(.is-active):hover {
                        color: #32c5ff;
                        background-color: rgba(50, 197, 255, 0.08);
                    }

                    // 激活状态
                    &.is-active {
                        color: #32c5ff;
                        font-weight: 500;
                        background-color: rgba(50, 197, 255, 0.1);

                        &::after {
                            // 激活指示条
                            content: '';
                            position: absolute;
                            left: 0;
                            top: 0;
                            bottom: 0;
                            width: 3px;
                            background: #32c5ff;
                        }
                    }
                }
            }
        }

        .right {
            flex: 1;
            overflow-y: auto;
            background: #fff;
            padding: 20px;
        }
    }

    .el-aside {
        background-color: #fff;

        &__logo {
            height: 120px;
            // background: url('@/assets/logo.png') no-repeat center / 120px auto;
        }

        .el-menu {
            border-right: none;
        }
    }

    .el-header {
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: flex-end;
        height: 40px;

        .el-dropdown__box {
            display: flex;
            align-items: center;

            .el-icon {
                color: #4c4141;
                margin-left: 20px;
            }

            &:active,
            &:focus {
                outline: none;
            }
        }
    }

    .el-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #666;
    }
}
</style>
