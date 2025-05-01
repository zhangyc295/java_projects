<template>
    <div class="exam-page flex-col">
        <div class="exam-selected-section flex-col">
            <div class="exam-option-group flex-row justify-between">
                <div class="exam-option" v-for="option in options" :key="option.value"
                    @click="selectOption(option.value)" :class="{ selected: selectedOption === option.value }">
                    {{ option.label }}
                </div>
            </div>
            <div class="center-box">
                <span class="exam-list-title">推荐竞赛</span>
                <!-- <el-form :inline="true" class="exam-navigation flex-row justify-between">
                    <el-form-item label="竞赛开始时间查询" prop="datetimerange" class="exam-navigation-box">
                        <el-date-picker style="width:360px;" v-model="params.datetimerange" type="datetimerange"
                            range-separator="⾄" start-placeholder="查询起始时间" end-placeholder="查询结束时间">
                        </el-date-picker>
                        
                    </el-form-item>
                    <el-form-item label="竞赛开始时间查询">
                        <el-date-picker v-model="params.startTime" type="datetime" placeholder="查询起始时间"
                            style="width: 180px; margin-right: 8px" />
                        <el-date-picker v-model="params.endTime" type="datetime" placeholder="查询结束时间"
                            style="width: 180px" />
                    </el-form-item>

                    <el-form-item>
                        <el-button @click="onSearch" plain type="primary">搜索</el-button>
                        <el-button @click="onReset" plain type="info">重置</el-button>
                    </el-form-item>
                </el-form> -->

                <el-form :inline="true" class="exam-navigation flex-row justify-between">
                    <el-form-item label="竞赛开始时间查询">
                        <el-date-picker v-model="params.startTime" type="datetime" placeholder="查询起始时间"
                            style="width:240px;" />
                    </el-form-item>

                    <el-form-item>
                        <el-date-picker v-model="params.endTime" type="datetime" placeholder="查询结束时间"
                            style="width: 240px;" />
                    </el-form-item>

                    <el-form-item>
                        <el-button @click="onSearch" plain type="primary">搜索</el-button>
                        <el-button @click="onReset" plain type="info">重置</el-button>
                    </el-form-item>
                </el-form>

                <!-- </div> -->
                <div class="exam-list-group flex-row">
                    <div class="exam-list-item flex-col" v-for="(contest, index) in contestList" :key="index">
                        <div>
                            <img src="@/assets/images/exam.png">
                        </div>
                        <div class="right-info">
                            <span class="exam-title">{{ contest.title }}</span>
                            <div class="exam-content flex-col justify-between">
                                <span>开赛时间：{{ contest.startTime }}</span>
                                <span>结束时间：{{ contest.endTime }}</span>
                            </div>
                            <div class="exam-button-container">
                                <span class="exam-hash-entry"
                                    v-if="isOngoingAndUnregisteredCompetition(contest)">已开赛</span>
                                <span class="exam-hash-registy" v-if="isEntryAndNotStart(contest)">已报名</span>
                                <div v-if="isHistoryContest(contest)">
                                    <el-button class="exam-practice-button" type="primary" plain
                                        @click="goContest(contest)">竞赛练习</el-button>
                                    <el-button class="exam-rank-button" type="primary" plain
                                        @click="togglePopover(contest.contestId)">查看排名</el-button>
                                </div>
                                <el-button class="exam-enter-button" type="primary" plain v-if="isStartContest(contest)"
                                    @click="goContest(contest)">开始答题</el-button>
                                <el-button class="exam-enter-button" type="primary" plain v-if="isNotEntry(contest)"
                                    @click="enterContest(contest.contestId)">报名参赛</el-button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="exam-page-pagination flex-row">
                <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total"
                    v-model:current-page="params.pageNumber" v-model:page-size="params.pageSize" :page-sizes="[3, 6, 9]"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </div>
    </div>

    <el-dialog v-model="dialogVisible" width="600px" top="30vh" :show-close="true" :close-on-click-modal="false"
        :close-on-press-escape="false" class="oj-login-dialog-centor" center>
        <el-table :data="contestRankList">
            <el-table-column label="排名" prop="contestRank" />
            <el-table-column label="用户昵称" prop="nickName" />
            <el-table-column label="用户得分" prop="score" />
        </el-table>
        <el-pagination class="range_page" background layout="total, sizes, prev, pager, next, jumper" :total="rankTotal"
            v-model:current-page="rankParams.pageNumber" v-model:page-size="rankParams.pageSize"
            :page-sizes="[5, 10, 20]" @size-change="handleRankSizeChange"
            @current-change="handleRankCurrentChange" />
    </el-dialog>
</template>
<script setup>
import { reactive, ref } from 'vue'
import { getContestListService, enterContestService ,getContestRankListService} from '@/api/contest'
import { getToken } from '@/utils/cookie'
import { getUserInfoService } from '@/api/user'
import { useRouter } from 'vue-router'
//参数定义
const contestList = ref([]) //题⽬列表
const total = ref(0)
const selectedOption = ref(0); // 初始化选中的⽂本
const router = useRouter()

const options = ref([
    { label: '未完赛', value: 0 },
    { label: '历史竞赛', value: 1 },
])
const params = reactive({
    pageNumber: 1,
    pageSize: 9,
    type: 0,
    startTime: '',
    endTime: '',
})

const rankParams = reactive({
  contestId:'',
  pageNumber: 1,
  pageSize: 10,
})

const contestRankList = ref([])
const rankTotal = ref(0)

// 分页
function handleRankSizeChange(newSize) {
  rankParams.pageNumber = 1
  getContestRankList()
}

function handleRankCurrentChange(newPage) {
  getContestRankList()
}


const dialogVisible = ref(false)

async function getContestRankList() {
  const result = await getContestRankListService(rankParams)
  contestRankList.value = result.rows
  rankTotal.value = result.total
}

function togglePopover(contestId) {
  dialogVisible.value = true
  rankParams.contestId = contestId
  getContestRankList()
}

//竞赛列表
async function getExamList() {
    const result = await getContestListService(params)
    contestList.value = result.rows
    total.value = result.total
    // console.log(result)
}
getExamList()

async function selectOption(type) {
    selectedOption.value = type
    params.pageNumber = 1
    params.type = type
    getExamList()
}

// 搜索/重置
function onSearch() {
    params.pageNumber = 1


    getExamList()
}


function onReset() {
    params.pageNumber = 1
    params.pageSize = 9
    params.type = 0
    params.startTime = ''
    params.endTime = ''
    getExamList()
}

// 分⻚
function handleSizeChange(newSize) {
    params.pageNumber = 1
    getExamList()
}

function handleCurrentChange(newPage) {
    params.pageNumber = newPage // 应该更新当前页码
    getExamList()
}


function isOngoingAndUnregisteredCompetition(contest) {
    const now = new Date(); //当前时间
    return new Date(contest.startTime) < now && new Date(contest.endTime) > now && !contest.enter
}

function isEntryAndNotStart(contest) {
    const now = new Date();
    return new Date(contest.startTime) > now && contest.enter
}

function isHistoryContest(contest) {
    const now = new Date();
    return new Date(contest.endTime) < now;
}

function isStartContest(contest) {
    const now = new Date();
    return new Date(contest.startTime) < now && new Date(contest.endTime) > now && contest.enter;
}

function isNotEntry(contest) {
    const now = new Date();
    return new Date(contest.startTime) > now && !contest.enter;
}

const isLogin = ref(false)
async function checkLogin() {
    if (getToken()) {
        await getUserInfoService()
        isLogin.value = true
    }
}

async function enterContest(contestId) {
    try {
        await checkLogin()
        if (!isLogin.value) {
            ElMessage.error('请先登录后报名参赛，谢谢')
            return
        }

        const enterContestDTO = {
            contestId: contestId
        }

        await enterContestService(enterContestDTO)
        ElMessage.success('您已报名成功，请按时参赛')

        getExamList() // 报名成功后刷新列表
    } catch (error) {
        ElMessage.error('报名失败，请稍后重试')
    }
}
function goContest(contest) {
    router.push(`/client-oj/answer?contestId=${contest.contestId}&title=${contest.title}&endTime=${contest.endTime}`)
}

</script>


<style lang="scss" scoped>
.exam-page {
    background-color: rgba(247, 247, 247, 1);
    position: relative;
    overflow: hidden;
    display: flex;

    .center-box {
        max-width: 1520px;
        margin: 0 auto;
        width: 100%;
        min-height: 368px;
        background: #FFFFFF;
        border-radius: 16px;
        padding: 0 20px;
        padding-top: 30px;
        margin-top: 10px;
    }

    .exam-selected-section {
        margin: 0 auto;
        margin-bottom: 20px;
        position: relative;
        padding-top: 50px;
        max-width: 1520px;
        width: 100%;

        .exam-option-group {
            width: fit-content;
            height: 50px;
            position: absolute;
            top: 0;
            left: 0;

            .exam-option {
                cursor: pointer;
                padding: 10px;
                border-bottom: 2px solid transparent;
                transition: all 0.3s ease;
                font-family: PingFangSC, PingFang SC;
                font-weight: 600;
                font-size: 18px;
                color: #222;
                height: 50px;
                width: fit-content;
                display: flex;
                justify-content: center;
                align-items: center;
                margin-right: 20px;
            }

            .exam-option.selected {
                color: #32C5FF;
                border-bottom: 2px solid #32C5FF;
            }
        }

        .exam-list-title {
            height: 24px;
            font-family: PingFangSC, PingFang SC;
            font-weight: 600;
            font-size: 18px;
            color: #222222;
            line-height: 25px;
            text-align: left;
            margin-bottom: 30px;
            display: block;
        }

        :deep(.exam-navigation) {
            width: 750px;
            height: 40px;
            font-size: 100px;
            margin-bottom: 30px;

            .el-form-item {
                margin-right: 20px;
            }

            .el-form-item__label {
                background: #fff;
            }

            .exam-navigation-box {
                background-color: rgba(242, 243, 244, 1);
                border-radius: 6px;
                height: 30px;
                // width: 460px;
                font-weight: 700;
            }
        }

        .exam-list-group {
            // width: 1200px;
            flex-wrap: wrap;

            @media screen and (min-width: 1420px) {
                .exam-list-item {
                    width: 32%;

                    &:nth-of-type(3n) {
                        margin-right: 0;
                    }
                }

            }

            @media screen and (max-width: 1419px) {
                .exam-list-item {
                    width: 48%;
                    margin-right: 2%;

                    &:nth-of-type(2n) {
                        margin-right: 0;
                    }
                }
            }

            @media screen and (max-width: 970px) {
                .exam-list-item {
                    width: 100%;
                    margin-right: 0;
                }
            }

            .exam-list-item {
                height: 220px;
                background: #F9F9F9;
                border-radius: 10px;
                margin-right: 2%;
                margin-bottom: 20px;
                padding: 20px;
                box-sizing: border-box;
                display: flex;
                align-items: center;
                flex-direction: row;
                justify-content: space-between;
                cursor: pointer;

                .right-info {
                    width: calc(100% - 146px);
                }

                .exam-title {
                    height: 26px;
                    font-family: PingFangSC, PingFang SC;
                    font-weight: 600;
                    font-size: 18px;
                    color: #222222;
                    line-height: 26px;
                    text-align: left;
                    max-width: 90%;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    margin-bottom: 16px;
                    display: block;
                }

                .exam-content {
                    margin-bottom: 26px;

                    span {
                        height: 22px;
                        font-family: PingFangSC, PingFang SC;
                        font-weight: 400;
                        font-size: 16px;
                        color: #666666;
                        line-height: 22px;
                        display: block;
                        margin-bottom: 12px;
                    }

                }

                img {
                    width: 126px;
                    height: 180px;
                    border-radius: 6px;
                }

                &:hover {
                    background: #fff;
                    box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.1);

                    .exam-title {
                        color: #32C5FF;
                    }

                    .el-button {
                        background: #f7f7f7;
                    }
                }

                .exam-hash-entry {
                    float: right;
                    font-size: 18px;
                    font-family: PingFangSC-Regular, PingFang SC;
                    font-weight: 400;
                    color: #32C5FF;
                    ;
                }

                .exam-hash-registy {
                    float: right;
                    font-size: 18px;
                    font-family: PingFangSC-Regular, PingFang SC;
                    font-weight: 400;
                    color: #666666;
                }



                .exam-button-container {
                    display: flex;
                    justify-content: space-between;
                    /* 或者使用 flex-start 来紧密排列按钮 */
                    align-items: center;

                    /* 如果需要垂直居中 */
                    /* 其他样式，如外边距、内边距等 */
                    .el-button {
                        width: 120px;
                        height: 44px;
                        background: #F7F7F7;
                        border-radius: 4px;
                        border: 1px solid #32C5FF;
                        font-family: PingFangSC, PingFang SC;
                        font-weight: 400;
                        font-size: 18px;
                        color: #32C5FF;
                        line-height: 44px;
                        text-align: center;
                    }
                }
            }
        }

        .exam-page-pagination {
            width: 594px;
            height: 40px;
            margin: 30px 0 73px 800px;
        }
    }
}
</style>