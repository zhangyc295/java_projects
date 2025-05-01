<template>
    <div class="add-contest-component-box">
        <div class="add-contest-component">
            <!-- 竞赛信息模块 -->
            <div class="contest-base-info-box">
                <!-- 标题 -->
                <div class="contest-base-title">
                    <span class="base-title">{{ type === 'edit' ? '编辑竞赛' : '添加竞赛' }}</span>
                    <span class="go-back" @click="goBack">返回</span>
                </div>
                <!-- 基本信息 -->
                <div class="contest-base-info">
                    <div class="group-box">
                        <div class="group-item">
                            <div class="item-label required">竞赛名称</div>
                            <div>
                                <el-input v-model="formContest.title" style="width:370px"
                                    placeholder="请填写竞赛名称"></el-input>
                            </div>
                        </div>
                    </div>
                    <div class="group-box">
                        <div class="group-item">
                            <div class="item-label required">竞赛周期</div>
                            <div>
                                <!-- <el-date-picker v-model="formContest.contestDate" :disabledDate="disabledDate"
                                    type="datetimerange" start-placeholder="竞赛开始时间" end-placeholder="竞赛结束时间"
                                    value-format="YYYY-MM-DD HH:mm:ss" />
                                     -->
                                <el-date-picker v-model="formContest.startTime" type="datetime" placeholder="竞赛开始时间"
                                    style="width: 180px; margin-right: 10px" />
                                <el-date-picker v-model="formContest.endTime" type="datetime" placeholder="竞赛结束时间"
                                    style="width: 180px" />
                            </div>
                        </div>
                    </div>
                    <div class="group-box">
                        <div class="group-item">
                            <el-button class="contest-base-info-button" type="primary" plain
                                @click="saveInfo">保存</el-button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 添加竞赛题目 -->
            <div class="contest-select-question-box">
                <el-button class="contest-add-question" :icon="Plus" link type="primary" @click="addQuestion()">
                    添加题目
                </el-button>
                <el-table height="320px" :data="formContest.contestQuestionList" class="question-select-list">
                    <el-table-column prop="questionId" width="180px" label="题目id" />
                    <el-table-column prop="title" :show-overflow-tooltip="true" label="题目标题" />
                    <el-table-column prop="difficulty" width="120px" label="题目难度">
                        <template #default="{ row }">
                            <div v-if="row.difficulty === 1" style="color:#3EC8FF;">简单</div>
                            <div v-if="row.difficulty === 2" style="color:#FE7909;">中等</div>
                            <div v-if="row.difficulty === 3" style="color:#FD4C40;">困难</div>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="80px">
                        <template #default="{ row }">
                            <el-button circle link type="danger"
                                @click="deleteContestQuestion(formContest.contestId, row.questionId)"> 删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <!-- 题目配置模块 题目列表勾选加序号 -->
            <div>
                <el-dialog v-model="dialogVisible">
                    <div class="contest-list-box">
                        <div class="contest-list-title required">选择竞赛题目</div>
                        <el-form :inline="true">
                            <el-form-item label="题目难度">
                                <selector v-model="params.difficulty" style="width: 120px;"></selector>
                            </el-form-item>
                            <el-form-item label="题目名称">
                                <el-input v-model="params.title" placeholder="请您输入要搜索的题目" />
                            </el-form-item>
                            <el-form-item>
                                <el-button @click="onSearch" plain>搜索</el-button>
                                <el-button @click="onReset" plain type="info">重置</el-button>
                            </el-form-item>

                        </el-form>
                        <!-- 题目列表 -->
                        <el-table :data="questionList" @select="handleRowSelect">
                            <el-table-column type="selection"></el-table-column>
                            <el-table-column prop="questionId" label="题目id" />
                            <el-table-column prop="title" label="题目标题" />
                            <el-table-column prop="difficulty" label="题目难度">
                                <template #default="{ row }">
                                    <div v-if="row.difficulty === 1" style="color:#3EC8FF;">简单</div>
                                    <div v-if="row.difficulty === 2" style="color:#FE7909;">中等</div>
                                    <div v-if="row.difficulty === 3" style="color:#FD4C40;">困难</div>
                                </template>
                            </el-table-column>
                        </el-table>
                        <!-- 分页区域 -->
                        <div class="contest-question-list-button">
                            <el-pagination background size="small" layout="total, sizes, prev, pager, next, jumper"
                                :total="total" v-model:current-page="params.pageNumber"
                                v-model:page-size="params.pageSize" :page-sizes="[1, 5, 10]"
                                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
                            <el-button class="question-select-submit" type="primary" plain
                                @click="submitSelectQuestion">提交</el-button>
                        </div>
                    </div>
                </el-dialog>
            </div>

            <!-- 提交任务区域 -->
            <div class="submit-box absolute">
                <el-button type="info" plain @click="goBack">取消</el-button>
                <el-button type="primary" plain @click="publishContestInfo">发布竞赛</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { contestAdd, addContestQuestion, getContestDetail, editContestQuestion, 
    delContestQuestion, publishContest} from "@/api/contest"
// import { contestAdd, addContestQuestionService, getContestDetailService, editContestService, 
// delContestQuestionService, publishContestService } from "@/api/contest"
import { getQuestionList } from "@/api/question"
import Selector from "@/components/QuestionSelector.vue"
import router from '@/router'
import { reactive, ref } from "vue"
import { Plus } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router';
import dayjs from 'dayjs'
// import { sortUserPlugins } from "vite"

const type = useRoute().query.type
const formContest = reactive({
    contestId: '',
    title: '',
    startTime: '',
    endTime: '',
    contestQuestionList: []
})

const params = reactive({
    pageNumber: 1,
    pageSize: 10,
    difficulty: '',
    title: '',
    selectedQuestionId: ''
})

// 返回
function goBack() {
    router.go(-1)
}

async function saveInfo() {
    const contestData = new FormData()
    // console.log(formContest)
    for (let key in formContest) {
        if (key === 'startTime' || key === 'endTime') {
            contestData.append(key, dayjs(formContest[key]).format('YYYY-MM-DD HH:mm:ss'))
        } else {
            contestData.append(key, formContest[key])
        }
    }
    // console.log("----------------")

    // console.log(formContest)
    if (formContest.contestId) {
        //编辑
        await editContestQuestion(contestData)
    } else {
        const addResult = await contestAdd(contestData)
        formContest.contestId = addResult.data
    }
    ElMessage.success('基本信息保存成功')
}

const questionList = ref([])
const total = ref(0)
async function getQuestion() {
    const result = await getQuestionList(params)
    // console.log(result)
    questionList.value = result.rows
    total.value = result.total
}

const dialogVisible = ref(false)
function addQuestion() {
    if (formContest.contestId === null || formContest.contestId === '') {
        ElMessage.error('请先保存竞赛基本信息')
    } else {
        getQuestion()
        dialogVisible.value = true
    }
}

function handleSizeChange() {
    params.pageNum = 1
    getQuestion()
}

function handleCurrentChange() {
    getQuestion()
}


function onSearch() {
    params.pageNum = 1
    getQuestion()
}

function onReset() {
    params.pageNumber = 1
    params.pageSize = 10
    params.title = ''
    params.difficulty = ''
    getQuestion()
}

async function publishContestInfo() {
    await publishContest(formContest.contestId)
    ElMessage.success('竞赛发布成功')
    router.push("/oj/system/contest")
}

const questionId = ref([])

function handleRowSelect(selection) {
    questionId.value = []
    selection.forEach(element => {
        questionId.value.push(element.questionId)
    });
}

async function submitSelectQuestion() {
    if (questionId.value && questionId.value.length < 1) {
        ElMessage.error('请先选择要提交的题目')
        return false
    }
    const contestInfo = reactive({
        contestId: formContest.contestId,
        questionId: questionId.value
    })
    // console.log(contestInfo)
    await addContestQuestion(contestInfo);
    getContestDetailById(formContest.contestId)
    dialogVisible.value = false
    ElMessage.success('竞赛题目添加成功')
}

async function getContestDetailInfo() {
    const contestId = useRoute().query.contestId
    // console.log(contestId)
    if (contestId) {
        formContest.contestId = contestId
        getContestDetailById(contestId)
        // console.log(formContest.contestQuestionList)
    }
}
getContestDetailInfo()

async function deleteContestQuestion(contestId, questionId) {
    await delContestQuestion(contestId, questionId)
    getContestDetailById(contestId)
    ElMessage.success('竞赛题目删除成功')
}

async function getContestDetailById(contestId) {

    const contestDetail = await getContestDetail(contestId)
    formContest.contestQuestionList = []
    // console.log(contestDetail)
    // 将 ID 拼接
    if (contestDetail.data.contestQuestionList) {
        const questionIds = contestDetail.data.contestQuestionList.map(q => q.questionId).join('_')
        // 存进你已有的 reactive 对象中
        params.selectedQuestionId = questionIds
    }


    // console.log(params.selectedQuestionId)
    // 输出： "1_2_3_4"
    // console.log(contestId)
    Object.assign(formContest, contestDetail.data)
    // formContest.startTime = contestDetail.data.startTime
    // formContest.endTime = contestDetail.data.endTime
}

</script>

<style lang="scss" scoped>
.add-contest-component-box {
    height: 100%;
    overflow: hidden;
    position: relative;
}

.contest-list-box {
    background: #fff;
    padding: 20px 24px;

    .question-select-submit {
        margin-left: 0;
        margin-top: 20px;
        width: 100%;
    }

    .contest-list-title {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        position: relative;
        padding: 15px 20px;
        padding-top: 0;

        &.required::before {
            position: absolute;
            content: '*';
            font-size: 20px;
            color: red;
            left: 10px;
        }
    }
}

.add-contest-component {
    width: 100%;
    background: #fff;
    padding-bottom: 120px;
    overflow-y: auto;
    box-sizing: border-box;
    height: calc(100vh - 50px);
    margin-top: -10px;

    .contest-select-question-box {

        background: #fff;
        border-bottom: 1px solid #fff;
        border-radius: 2px;
        width: 100%;

        .contest-add-question {
            font-size: 14px;
            float: right;
            margin: 10px 20px 5px 0;
        }

        .question-select-list {
            margin: 0 0 20px 0;
            height: 200px;
        }
    }

    .contest-base-info-box {
        background: #fff;
        border-bottom: 1px solid #fff;
        border-radius: 2px;
        margin-bottom: 10px;
        width: 100%;
        box-sizing: border-box;

        .contest-base-title {
            width: 100%;
            box-sizing: border-box;
            height: 52px;
            border-bottom: 1px solid #e9e9e9;
            display: flex;
            justify-content: space-between;
            align-items: center;

            .base-title {
                font-size: 16px;
                font-weight: 500;
                color: #333333;
            }

            .go-back {
                color: #999;
                cursor: pointer;
            }
        }

        .contest-base-info {
            box-sizing: border-box;
            border-bottom: 1px solid #e9e9e9;
        }

        .mesage-list-content {
            box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.1);
            background-color: rgba(255, 255, 255, 1);
            border-radius: 10px;
            width: 1200px;
            margin-top: 20px;
        }
    }

    .group-box {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: calc(100% - 64px);
        margin: 24px 0;

        .group-item {
            display: flex;
            align-items: center;
            width: 100%;

            .contest-base-info-button {
                margin-left: 104px;
                width: 370px;
            }

            .item-label {
                font-size: 14px;
                font-weight: 400;
                width: 94px;
                text-align: left;
                color: rgba(0, 0, 0, 0.85);
                position: relative;
                padding-left: 10px;

                &.required::before {
                    position: absolute;
                    content: '*';
                    font-size: 20px;
                    color: red;
                    left: 0px;
                    top: -2px;
                }
            }
        }
    }

    .submit-box {
        display: flex;
        align-items: center;
        justify-content: center;
        background: transparent;

        &.absolute {
            position: absolute;
            width: calc(100% - 48px);
            bottom: 0;
            background: #fff;
            z-index: 999;
        }
    }
}
</style>

<style>
.w-e-text-container {
    min-height: 142px;
}
</style>