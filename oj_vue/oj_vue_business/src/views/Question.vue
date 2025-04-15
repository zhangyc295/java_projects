<template>
    <el-form :inline="true">
        <el-form-item>
            <selector v-model="params.difficulty" placeholder="请选择题⽬难度" style="width: 200px;"></selector>
        </el-form-item>
        <el-form-item>
            <el-input v-model="params.title" placeholder="请您输⼊要搜索的题⽬" />
        </el-form-item>
        <el-form-item>
            <el-button plain @click="searchQuestion">搜索</el-button>
            <el-button plain @click="resetTable" type="info">重置</el-button>
            <el-button plain @click="showDrawer" type="primary" :icon="Plus">添加题⽬</el-button>
            <el-button index="/oj/system/question/deleted" plain @click="showQuestionInBin" type="primary"
                :icon="Delete">已删除题目</el-button>
        </el-form-item>
    </el-form>
    <el-table height="526px" :data="questionList">
        <el-table-column prop="questionId" width="180px" label="题⽬序号" />
        <el-table-column prop="title" label="题⽬标题" />
        <el-table-column prop="difficulty" label="题⽬难度" width="90px">
            <template #default="{ row }">
                <div v-if="row.difficulty === 1" style="color:#0FA535;">简单</div>
                <div v-if="row.difficulty === 2" style="color:#FE7909;">中等</div>
                <div v-if="row.difficulty === 3" style="color:#FD4C40;">困难</div>
            </template>
        </el-table-column>
        <el-table-column prop="nickName" label="创建⼈" width="140px" />
        <el-table-column prop="createTime" label="创建时间" width="180px" :formatter="formatTime" />
        <el-table-column label="操作" width="120px" fixed="right">
            <template #default="{ row }">
                <div style="display: flex; gap: 8px; align-items: center;">
                    <el-button link type="primary" @Click="onEdit(row.questionId)"
                        style="font-size: 14px; padding: 0;">编辑</el-button>
                    <el-button link type="danger" @Click="onDeleteToBin(row.questionId)"
                        style="font-size: 14px; padding: 0;">删除</el-button>
                </div>
            </template>
        </el-table-column>
    </el-table>
    <el-pagination background size="small" layout="total, sizes, prev, pager, next, jumper" :total="total"
        v-model:current-page="params.pageNumber" v-model:page-size="params.pageSize" :page-sizes="[1, 5, 10, 20]"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    <question-drawer ref="showQuestionDrawer" @success="onSuccess"></question-drawer>

    <el-drawer v-model="showDeletedDrawer" size="60%" title="已删除题目列表">
        <el-table :data="DeletedQuestionList" style="width: 100%;">
            <el-table-column prop="questionId" label="题目ID" width="220" />
            <el-table-column prop="title" label="题目标题" />
            <el-table-column prop="difficulty" label="题目难度" width="100">
                <template #default="{ row }">
                    <div v-if="row.difficulty === 1" style="color:#0FA535;">简单</div>
                    <div v-if="row.difficulty === 2" style="color:#FE7909;">中等</div>
                    <div v-if="row.difficulty === 3" style="color:#FD4C40;">困难</div>
                </template>
            </el-table-column>
            <el-table-column prop="nickName" label="删除人" width="140px" />
            <el-table-column prop="updateTime" label="删除时间" width="180px" :formatter="formatTime" />
            <!-- 可扩展操作列 -->
            <el-table-column label="操作" width="150px" fixed="right">
                <template #default="{ row }">
                    <el-button link type="success" @click="recoverQuestion(row.questionId)">恢复</el-button>
                    <el-button link type="danger" @click="removeQuestion(row.questionId)">彻底删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-drawer>

</template>

<script setup>
import { Delete, Plus } from "@element-plus/icons-vue"
import Selector from "@/components/QuestionSelector.vue"

import { delQuestionDetail, getQuestionList, getDeletedQuestionList, deleteQuestionForever, recoverQuestionInBin } from "@/api/question";
import { reactive, ref } from "vue";
import QuestionDrawer from "@/components/QuestionDrawer.vue";

const params = reactive({
    pageNumber: 1,
    pageSize: 10,
    difficulty: '',
    title: ''
})

const questionList = ref([])
const total = ref(0)
const DeletedQuestionList = ref([])
async function getQuestion() {
    const result = await getQuestionList(params)
    console.log(result)
    questionList.value = result.rows
    total.value = result.total
    // console.log(total.value)
}
getQuestion()

function searchQuestion() {
    params.pageNumber = 1
    getQuestion()
}

function resetTable() {
    params.pageNumber = 1
    params.pageSize = 10
    params.difficulty = ''
    params.title = ''
    getQuestion()
}

function handleSizeChange(newSize) {
    // params.pageSize = newSize   v-model双向绑定
    params.pageNumber = 1
    getQuestion()
}

function handleCurrentChange(newPage) {
    // params.pageNumber = newPage
    getQuestion()
}


function formatTime(row) {
    // 防止值为 null
    if (row.createTime) {
        return row.createTime.replace('T', ' ')
    }
    if (row.updateTime) {
        return row.updateTime.replace('T', ' ')
    }
    return ''
}


const showQuestionDrawer = ref()
function showDrawer() {
    showQuestionDrawer.value.open()
}

function onSuccess(choice) {
    if (choice === 'add') {
        params.pageNumber = 1
    }
    getQuestion()
}

async function onEdit(questionId) {
    showQuestionDrawer.value.open(questionId)
}
async function onDeleteToBin(questionId) {
    await delQuestionDetail(questionId)
    params.pageNumber = 1
    getQuestion()
}

// async function showQuestionInBin() {
//     const result = await getDeletedQuestionList(params)
//     console.log(result)
//     DeletedQuestionList.value = result.rows
//     total.value = result.total
//     // console.log(total.value)
// } 

const showDeletedDrawer = ref(false)

async function showQuestionInBin() {
    const result = await getDeletedQuestionList(params)
    DeletedQuestionList.value = result.rows
    // console.log('DeletedQuestionList', DeletedQuestionList.value)
    showDeletedDrawer.value = true // 打开抽屉
}

// 示例恢复 / 删除方法（假设你后端已支持）
async function recoverQuestion(id) {
    await recoverQuestionInBin(id)
    showQuestionInBin() 
    getQuestion()
    // 刷新
}

async function removeQuestion(id) {
    await deleteQuestionForever(id)
    showQuestionInBin()
}

</script>

// const questionList = reactive([
// {
// questionId: '1',
// title: '题⽬1',
// difficulty: 1,
// createName: '超级管理员1',
// createTime: '2025-01-31 10:00:00'
// },
// {
// questionId: '2',
// title: '题⽬2',
// difficulty: 2,
// createName: '超级管理员1',
// createTime: '2025-02-28 11:00:00'
// },
// {
// questionId: '3',
// title: '题⽬3',
// difficulty: 3,
// createName: '超级管理员2',
// createTime: '2025-03-01 12:00:00'
// }
// ])
