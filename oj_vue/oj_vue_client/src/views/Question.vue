<template>
    <div class="index-question">
        <div class="index-question-table-box">
            <div class="left">
                <el-form :inline="true" ref="formModel" :model="form">
                    <el-form-item style="max-width:864px" >
                        <el-input v-model="params.keyword" placeholder="请您输入搜索的题目或内容"
                            style="width: 100%; margin: 20px 0 20px 10px;" />
                    </el-form-item class="question-header">
                    <el-form-item style="width: 180px;">
                        <selector v-model="params.difficulty" placeholder="请选择题⽬难度"></selector>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" plain @click="onSearch">搜索</el-button>
                        <el-button type="info" plain @click="onReset">重置</el-button>
                    </el-form-item>
                </el-form>
                <!-- 表格 -->
                <el-table :data="questionList" height="580px" style="width: 100%">
                    <el-table-column align="center" width="100px" label="序号">
                        <template #default="{ $index }">
                            {{ (params.pageNumber - 1) * params.pageSize + $index + 1 }}
                        </template>
                    </el-table-column>
                    <el-table-column align="left" prop="title" :show-overflow-tooltip="true" label="题目标题" />
                    <el-table-column align="center" width="180px" prop="difficulty" label="题目难度">
                        <template #default="{ row }">
                            <div style="color:#0FA535;" v-if="row.difficulty === 1">简单</div>
                            <div style="color:#FE7909;" v-if="row.difficulty === 2">中等</div>
                            <div style="color:#FD4C40;" v-if="row.difficulty === 3">困难</div>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center" width="180px">
                        <template #default="{ row }">
                            <el-button link type="primary" plain v-if="isLogin" @click="goQuestTest(row.questionId)">
                                开始答题
                            </el-button>
                            <span style="color:#9E9E9E;" v-else>请登录后参与答题</span>
                        </template>
                    </el-table-column>
                </el-table>
                <!-- 分页区域 -->
                <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total"
                    v-model:current-page="params.pageNumber" v-model:page-size="params.pageSize"
                    :page-sizes="[5, 10, 20]" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    style="margin:20px 100px 20px 0; justify-content: flex-end" />
            </div>
            <div class="right">
                <div class="top-box">
                    <el-calendar v-model="today"> </el-calendar>
                </div>
                <div class="bot-box">
                    <div class="title"><img width="96px" height="24px" src="@/assets/rebang.png" alt=""></div>
                    <div class="hot-list">
                        <div class="list-item" v-for="(item, index) in hotQuestionList" :key="'hot_' + index">
                            <img class="index-box" v-if="index == 0" src="@/assets/images/icon_1.png" alt="" >
                            <img class="index-box" v-if="index == 1" src="@/assets/images/icon_2.png" alt="" >
                            <img class="index-box" v-if="index == 2" src="@/assets/images/icon_3.png" alt="" >
                            <span class="index-box" v-if="index == 3">{{ index + 1 }}</span>
                            <span class="index-box" v-if="index == 4">{{ index + 1 }}</span>
                            <span class="txt" :title="item.title">{{ item.title }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import Selector from "@/components/QuestionSelector.vue"
import { getQuestionListService, getHotQuestionListService } from '@/api/question'
import { getToken } from "@/utils/cookie"
import router from "@/router"
import { getUserInfoService } from "@/api/user"



const form = reactive({
 
})
const params = reactive({
    pageNumber: 1,
    pageSize: 10,
    difficulty: '',
    keyword: ''
})
let today = ref(new Date()) //日历默认今天
const questionList = ref([]) //题目列表
const total = ref(0)
const isLogin = ref(false)

async function checkLogin() {
    if (getToken()) {
        await getUserInfoService()
        isLogin.value = true
    }
}
checkLogin()

async function getQuestionList() {
    const result = await getQuestionListService(params)
    questionList.value = result.rows
    total.value = result.total
}
getQuestionList()

const hotQuestionList = ref([])
async function getHotQuestionList() {
    const hotRef = await getHotQuestionListService()
    // console.log(hotRef)
    hotQuestionList.value = hotRef.rows
}

getHotQuestionList()

// 搜索/重置
function onSearch() {
    params.pageNumber = 1
    getQuestionList()
}

function onReset() {
    params.pageNumber = 1
    params.pageSize = 9
    params.difficulty = ''
    params.keyword = ''
    getQuestionList()
}

// 分页
function handleSizeChange(newSize) {
    console.log(params.pageSize)
    params.pageNum = 1
    getQuestionList()
}

function handleCurrentChange(newPage) {
    getQuestionList()
}

function goQuestTest(questionId) {
    // console.log("Navigating to question with ID:", questionId);
    router.push(`/client-oj/answer?questionId=${questionId}`)
}
</script>

<style lang="scss" scoped>
.index-question-table-box {
    display: flex;
    max-width: 1520px;
    width: 100%;
    justify-content: space-between;

    :deep(.el-pagination) {

        .el-select .el-select__wrapper,
        .el-input .el-input__wrapper {
            height: 24px;
        }
    }

    .right {
        width: 340px;

        :deep(.top-box) {
            width: 100%;
            height: 345px;
            padding-bottom: 20px;
            margin-bottom: 20px;
            border-radius: 16px;
            background: linear-gradient(164deg, #E2F7FF 0%, #FFFFFF 50%);

            .el-calendar,
            .el-calendar__body {
                background: transparent;
            }

            .el-calendar {
                position: relative;

                .el-calendar__title {
                    font-weight: bold;
                }

                .el-calendar__header {
                    border: none;
                    padding-bottom: 0;
                }

                .el-calendar__button-group {
                    text-align: center;
                    background: transparent;

                    .el-button-group>.el-button {
                        color: #1f2122;
                        border: none;
                        background: rgba(255, 255, 255, .1);
                    }
                }

                .el-calendar-table {
                    thead th {
                        color: #32C5FF;
                        font-size: 12px;
                        font-weight: bold;
                    }

                    .el-calendar-day {
                        height: 40px;
                        text-align: center;
                    }

                    td {
                        border: none;
                    }
                }
            }

        }

        .bot-box {
            width: 340px;
            border-radius: 16px;
            padding-top: 0;
            box-sizing: border-box;
            background: #fff;
            position: relative;
            overflow: hidden;

            .title {
                font-weight: bold;
                color: #32C5FF;
                position: relative;
                width: 100%;
                height: 60px;
                padding: 20px;
                padding-bottom: 0;
                background: linear-gradient(169deg, #E2F7FF 0%, #FFFFFF 100%);

                span {
                    position: absolute;
                    display: flex;
                    right: 20px;
                    font-family: PingFangSC, PingFang SC;
                    font-weight: 400;
                    font-size: 14px;
                    color: #32C5FF;
                    bottom: 20px;
                    cursor: pointer;
                }
            }

            .hot-list {
                width: calc(100% - 40px);
                margin: 0 auto;
                padding-top: 20px;
                padding-bottom: 20px;

                .list-item {
                    margin-bottom: 18px;
                    cursor: pointer;
                    display: flex;
                    align-items: center;

                    .index-box {
                        display: inline-block;
                        text-align: center;
                        width: 20px;
                        font-family: Tensentype-RuiHeiJ, Tensentype-RuiHeiJ;
                        font-weight: normal;
                        font-size: 18px;
                        color: #999999;
                        font-weight: bold;
                    }

                    .txt {
                        max-width: calc(100% - 34px);
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        margin-left: 10px;
                        font-family: PingFangSC, PingFang SC;
                        font-weight: 400;
                        font-size: 16px;
                        color: #222222;
                        line-height: 22px;
                        text-align: left;
                        font-style: normal;
                    }

                    // &:hover {
                    //     .txt {
                    //         color: #32C5FF;
                    //     }
                    // }
                }
            }
        }
    }

    :deep(.left) {
        width: calc(100% - 360px);
        border: none;
        border-radius: 16px;
        overflow: hidden;
        background: linear-gradient(177deg, #f3fcff 0%, #FFFFFF 10%);

        .el-table {
            th.el-table__cell {
                background-color: rgba(50, 197, 255, 0.08) !important;
                font-size: 13px;
            }

            td {
                height: 54px;
            }
        }

        .el-card__header {
            font-family: PingFangSC, PingFang SC;
            font-weight: 600;
            font-size: 20px;
            color: #222222;
            line-height: 28px;
            text-align: left;
            font-style: normal;
            border: none;
            padding-bottom: 0;
        }
    }
}

.el-input__wrapper {
    background-color: #F8F8F8;
}

.index-question {
    background-color: rgba(247, 247, 247, 1);
    display: flex;
    justify-content: center;
    margin-top: 20px;

    .no-border {
        border: none;
    }
}

.el-table {
    th {
        word-break: break-word;
        background-color: #033e7c !important;
        color: #ffff;
        height: 40px;
        font-weight: 700;
        font-size: 14px;
    }
}
</style>

<!-- npm install ace-builds@1.4.13 -->
<!-- <template>
    题目页面
</template> -->