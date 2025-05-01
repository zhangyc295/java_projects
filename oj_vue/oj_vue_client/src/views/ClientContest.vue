<template>
    <div class="my-exam-page">
        <div class="exam-list-block">
            <div class="exam-list-header">
                <span class="ex-title">我的竞赛</span>
                <span class="exam-list-back" @click="goBack">返回</span>
            </div>
            <div class="exam-list-content" v-for="(contest, index) in myContestList" :key="index">
                <img src="@/assets/ide/jingsai.png" class="image" />
                <div class="exam-content">
                    <div class="title">
                        {{ contest.title }}
                    </div>
                    <div class="date"><span>比赛时间:</span> {{ contest.startTime }} ~ {{ contest.endTime }}</div>
                </div>
                <div>
                    <div class="exam-end-lable-list" v-if="isHistoryContest(contest)">
                        <span class="exam-end-lable">已完赛</span>
                        <el-button class="exam-rank-lable" link type="primary"
                            @click="togglePopover(contest.contestId)">查看排名</el-button>
                        <el-button class="exam-rank-lable" link type="primary"
                            @click="goContest(contest)">竞赛练习</el-button>
                    </div>
                    <div class="exam-status-lable exam-end-lable-list" v-else-if="isNotStart(contest)">未开赛</div>
                    <div class="exam-end-lable-list" v-else>
                        <el-button class="exam-rank-lable" link type="primary" plain
                            @click="goContest(contest)">开始答题</el-button>
                    </div>
                </div>
            </div>
            <div class="my-exam-pagination">
                <!-- 增加分页展示器 -->
                <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total"
                    v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[1, 5, 10]"
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
        v-model:current-page="rankParams.pageNumber" v-model:page-size="rankParams.pageSize" :page-sizes="[5, 10, 20]"
        @size-change="handleRankSizeChange" @current-change="handleRankCurrentChange" />
    </el-dialog>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { getContestListService, getContestRankListService } from "@/api/contest"
import router from '@/router'

const myContestList = ref([]) //消息列表

const total = ref(0)
const params = reactive({
    pageNumber: 1,
    pageSize: 10,
})

// 排名分页
function handleRankSizeChange(newSize) {
  rankParams.pageNumber = 1
  getContestRankList()
}

function handleRankCurrentChange(newPage) {
  getContestRankList()
}

// 分页
function handleSizeChange(newSize) {
    params.pageNumber = 1
    getmyContestList()
}

function handleCurrentChange(newPage) {
    getmyContestList()
}

//消息列表
async function getmyContestList() {
    const ref = await getContestListService(params)
    // console.log(ref)

    myContestList.value = ref.rows.filter(item => item.enter === true)
    total.value = myContestList.value.length
}
getmyContestList()

function isHistoryContest(contest) {
    const now = new Date();
    return new Date(contest.endTime) < now;
}

const isNotStart = (contest) => {
    const now = new Date(); //当前时间
    return new Date(contest.startTime) > now;
}

const goBack = () => {
    router.go(-1)
}

function goContest(contest) {
    router.push(`/client-oj/answer?contestId=${contest.contestId}&title=${contest.title}&endTime=${contest.endTime}`)
}

const dialogVisible = ref(false)
const contestRankList = ref([])
const rankTotal = ref(0)
const rankParams = reactive({
    contestId: '',
    pageNumber: 1,
    pageSize: 10,
})

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

</script>

<style lang="scss">
.my-exam-page {
    max-width: 1520px;
    margin: 0 auto;
    background-color: rgba(247, 247, 247, 1);
    position: relative;
    overflow: hidden;
    display: flex;
    justify-content: center;
}

.exam-list-block {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
}

.exam-list-header {
    background-color: rgba(255, 255, 255, 1);
    border-radius: 10px;
    width: 100%;
    /* 设置宽度为100%以确保水平居中 */
    height: 60px;
    font-size: 25px;
    text-indent: 20px;
    display: flex;
    align-items: center;
    margin-top: 20px;

    .ex-title {
        font-family: PingFangSC, PingFang SC;
        font-weight: 600;
        font-size: 18px;
        color: #222222;
    }

    .exam-list-back {
        cursor: pointer;
        color: #999999;
        font-size: 15px;
        margin-left: auto;
        padding-right: 20px;
    }
}

.exam-list-content {
    height: 110px;
    width: 100%;
    background: #FFFFFF;
    border-radius: 10px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    margin-top: 20px;
    justify-content: space-between;
    cursor: pointer;

    &:hover {
        box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.1);
    }

    img {
        width: 50px;
        height: 50px;
        margin: 0 20px;
    }
}

.exam-list-content .exam-content {
    max-width: calc(100% - 320px);
    width: 100%;
}

.exam-list-content .title {
    font-weight: bold;
    font-size: 16px;
    margin-bottom: 10px;
}

.exam-list-content .date {
    font-weight: 400;
    color: #000;
    line-height: 20px;
    margin-left: auto;
    font-size: 14px;

    span {
        color: #999;
    }

    /* 将日期推至行尾 */
}

.exam-list-content .exam-status-lable {
    color: #999999;
}

.exam-end-lable-list {
    padding-right: 20px;
    min-width: 228px;
    text-align: right;

    .exam-end-lable {
        margin-right: 10px;
        font-family: MicrosoftYaHei;
        font-size: 16px;
        color: #999999;

    }

    .exam-rank-lable {
        font-size: 16px;
    }

}

.my-exam-pagination {
    width: 100%;
    display: flex;
    justify-content: flex-end;
    padding-bottom: 20px;
}
</style>