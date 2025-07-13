<template>
    <!-- 表单 -->
    <el-form :inline="true">
        <el-form-item label="用户查询">
            <el-form-item label="用户id">
                <el-input v-model="params.userId" placeholder="请您输入要搜索的用户id" />
            </el-form-item>
            <el-form-item label="用户名">
                <el-input v-model="params.userName" placeholder="请您输入要搜索的用户名" />
            </el-form-item>
            <el-form-item>
                <el-button @click="onSearch" plain>搜索</el-button>
                <el-button @click="onReset" plain type="info">重置</el-button>
            </el-form-item>
        </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table height="526px" :data="userList">
        <el-table-column prop="userId" label="用户id" width="180px" />
        <el-table-column prop="userName" width="120px" label="用户名" />
        <el-table-column prop="nickName" width="120px" label="用户昵称" />
        <el-table-column prop="gender" width="80px" label="用户性别">
            <template #default="{ row }">
                <div v-if="row.gender === 1" style="color:#3EC8FF;">男</div>
                <div v-if="row.gender === 2" style="color:#FD4C40;">女</div>
            </template>
        </el-table-column>
        <el-table-column prop="telephone" width="120px" label="手机号" />
        <el-table-column prop="email" width="220px" label="邮箱" />
        <el-table-column label="学校/专业" width="280x">
            <template #default="{ row }">
                <span class="block-span"> 学校: {{ row.school }}</span>
                <span class="block-span"> 专业: {{ row.major }}</span>
            </template>
        </el-table-column>
        <el-table-column prop="introduction" label="个人介绍" />
        <el-table-column prop="status" width="90px" label="用户状态">
            <template #default="{ row }">
                <div v-if="row.status == 1"> <el-tag type="success">正常</el-tag> </div>
                <div v-if="row.status == 0"> <el-tag type="danger">拉黑</el-tag> </div>
                <!-- <el-tag type="success" v-if="row.status == 1">正常</el-tag>
                <el-tag type="danger" v-if="row.status == 0">拉黑</el-tag> -->
            </template>
        </el-table-column>
        <el-table-column label="操作" width="80px" fixed="right">
            <template #default="{ row }">
                <el-button v-if="row.status === 1" link type="danger"
                    @click="onUpdateUserStatus(row.userId, 0)">拉黑</el-button>
                <el-button v-if="row.status === 0" link type="primary"
                    @click="onUpdateUserStatus(row.userId, 1)">解禁</el-button>
            </template>
        </el-table-column>
    </el-table>
    <!-- 分页区域 -->
    <el-pagination background size="small" layout="total, sizes, prev, pager, next, jumper" :total="total"
        v-model:current-page="params.pageNumber" v-model:page-size="params.pageSize" :page-sizes="[1, 5, 10]"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
</template>

<script setup>
import { reactive, ref } from 'vue';
import { getUser, updateStatus } from '@/api/user'

const params = reactive({
    pageNumber: 1,
    pageSize: 10,
    userId: '',
    userName: '',
})

const userList = ref([])
const total = ref(0)

async function getUserList() {
    const ref = await getUser(params)
    userList.value = ref.rows
    total.value = ref.total
}
getUserList()

function onSearch() {
    params.pageNumber = 1
    getUserList()
}

function onReset() {
    params.pageNumber = 1
    params.pageSize = 10
    params.userId = ''
    params.userName = ''
    getUserList()
}

function handleSizeChange(newSize) {
    params.pageNumber = 1
    getUserList()
}

function handleCurrentChange(newPage) {
    getUserList()
}

const updateStatusParams = reactive({
    userId: '',
    status: '',
})

async function onUpdateUserStatus(userId, status) {
    updateStatusParams.userId = userId
    updateStatusParams.status = status
    await updateStatus(updateStatusParams)
    getUserList()
}
</script>