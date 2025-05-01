<template>
    <el-form :inline="true">
      <!-- <el-form-item label="创建日期">
          <el-date-picker unlink-panels v-model="datetimeRange" style="width: 240px" type="datetimerange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期">
          </el-date-picker>
      </el-form-item> -->
        <el-form-item label="竞赛开始时间查询">
            <el-date-picker v-model="params.startTime" type="datetime" placeholder="查询起始时间" style="width: 180px; margin-right: 8px"/>
            <el-date-picker v-model="params.endTime" type="datetime" placeholder="查询结束时间" style="width: 180px"/>
        </el-form-item>

        <el-form-item label="竞赛名称">
            <el-input v-model="params.title" placeholder="请您输入要搜索的竞赛名称" />
        </el-form-item>
        <el-form-item>
            <el-button @click="onSearch" plain>搜索</el-button>
            <el-button @click="onReset" plain type="info">重置</el-button>
            <el-button type="primary" :icon="Plus" plain @click="onAddContest">添加竞赛</el-button>
        </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table height="526px" :data="contestList">
        <el-table-column prop="title" label="竞赛标题" />
        <el-table-column prop="startTime" width="180px" label="竞赛开始时间" />
        <el-table-column prop="endTime" width="180px" label="竞赛结束时间" />
        <el-table-column label="是否开赛" width="100px">
            <template #default="{ row }">
                <div v-if = "isContestStart(row) == 1">
                    <el-tag type="success">进行中</el-tag>
                </div>
                <div v-if = "isContestStart(row) == 0">
                    <el-tag type="warning">未开赛</el-tag>
                </div>
                <div v-if = "isContestStart(row) == -1">
                    <el-tag type="info">已结束</el-tag>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="status" width="100px" label="是否发布">
            <template #default="{ row }">
                <div v-if="row.status == 0">
                    <el-tag type="danger">未发布</el-tag>
                </div>
                <div v-if="row.status == 1">
                    <el-tag type="success">已发布</el-tag>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="nickName" width="140px" label="创建用户" />
        <el-table-column prop="createTime" width="180px" label="创建时间" />
        <el-table-column label="操作" width="180px">
            <template #default="{ row }">
                <el-button v-if="isContestStart(row) == 0 && row.status == 0" link type="primary" @click="onEdit(row.contestId)">编辑</el-button>
                <el-button v-if="isContestStart(row) == 0 && row.status == 0" link type="danger" @click="onDelete(row.contestId)" >删除</el-button>
                <el-button v-if="isContestStart(row) == 0 && row.status == 1" link type="danger" @click="cancelPublishContestInfo(row.contestId)">撤销发布</el-button>
                <el-button v-if="isContestStart(row) == 0 && row.status == 0" link type="primary" @click="publishContestInfo(row.contestId)">发布</el-button>

                <!-- <el-button v-if="isContestStart(row) == 0 && row.status == 0" type="text"
                    @click="onEdit(row.contestId)">编辑
                </el-button>
                <el-button v-if="isContestStart(row) == 0 && row.status == 0" type="text"
                    @click="onDelete(row.contestId)" class="red">删除
                </el-button>
                <el-button v-if="row.status == 1 && isContestStart(row) == 0" type="text"
                    @click="cancelPublishContest(row.contestId)">撤销发布</el-button>
                <el-button v-if="row.status == 0 && isContestStart(row) == 0" type="text"
                    @click="publishContest(row.contestId)">发布</el-button> -->
                <!-- <el-button type="text" v-if="isContestStart(row) === 1" style="color: #67C23A !important;">已开赛，不允许操作</el-button>
                <el-button type="text" v-if="isContestStart(row) === -1" style="color: #606266 !important;">已结束，不允许操作</el-button> -->
                <el-button v-if="isContestStart(row) === 1" link style="color: #67C23A;">已开赛，不允许操作</el-button>
                <el-button v-if="isContestStart(row) === -1" link style="color: #606266;">已结束，不允许操作</el-button>

            </template>
        </el-table-column>
    </el-table>
    <!-- 分页区域 -->
    <el-pagination background size="small" layout="total, sizes, prev, pager, next, jumper" :total="total"
        v-model:current-page="params.pageNumber" v-model:page-size="params.pageSize" :page-sizes="[1, 5, 10]"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
// import { getContestList, delContestService, publishContestService, cancelPublishContestService } from '@/api/contest'
import { getContestList, getContestDetail, delContest, publishContest, cancelPublishContest} from '@/api/contest'
import { reactive,ref } from 'vue'
import router from '@/router'

function isContestStart(contest) {
    
    const now = new Date(); //当前时间
    if(now < new Date(contest.startTime)){
        return 0;  //未开赛
    }
    if(now >new Date(contest.endTime)){
        return -1; //已结束
    }
    if(now > new Date(contest.startTime) && now < new Date(contest.endTime)){
        return 1;  //进行中
    }
}

const params = reactive({
    pageNumber: 1,
    pageSize: 10,
    startTime:'',
    endTime:'',
    title: ''
})

const contestList = ref([])
const total = ref(0)
const datetimeRange = ref([])

async function getContest() {
    // if (datetimeRange.value[0] instanceof Date) {
    //     params.startTime = formatDateTime(datetimeRange.value[0])
    // }
    // if (datetimeRange.value[1] instanceof Date) {
    //     params.endTime = formatDateTime(datetimeRange.value[1])
    // }
    const result = await getContestList(params)
    contestList.value = result.rows
    // console.log('开始时间:', params.startTime)
    // console.log('结束时间:', params.endTime)
    total.value = result.total
}

getContest()

// function formatDateTime(date) {
//     const year = date.getFullYear();
//     const month = String(date.getMonth() + 1).padStart(2, '0');
//     const day = String(date.getDate()).padStart(2, '0');
//     const hours = String(date.getHours()).padStart(2, '0');
//     const minutes = String(date.getMinutes()).padStart(2, '0');
//     const seconds = String(date.getSeconds()).padStart(2, '0');
//     return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
// }

  
function handleSizeChange(newSize) {
    params.pageNumber = 1
    getContest()
}
  
function handleCurrentChange(newPage) {
    getContest()
}
  
function onSearch() {
    params.pageNumber = 1
    getContest()
}
  
function onReset() {
    params.pageNumber = 1
    params.pageSize = 10
    params.title = ''
    params.startTime = ''
    params.endTime = ''
    datetimeRange.value.length = 0
    getContest()
}
  
function onAddContest() {
    // console.log(contestId)
    //跳转到新的页面上   
    router.push("/oj/system/addContest?type=add")
    // router.push("/oj/system/addContest")
}
  
async function onEdit(contestId) {
    // await getContestDetail(contestId)
    // console.log(contestId)
    router.push(`/oj/system/addContest?type=edit&contestId=${contestId}`)  
    // 反引号
    ///oj/system/updateContest?type=edit&contestId=${contestId}
    ///oj/system/updateContest?type=edit&contestId=123
}
  
async function onDelete(contestId) {
    await delContest(contestId)
    params.pageNumber = 1
    getContest()
}
  
async function publishContestInfo(contestId) {
    await publishContest(contestId)
    getContest()
}
  
  async function cancelPublishContestInfo(contestId) {
    await cancelPublishContest(contestId)
    getContest()
  }
</script>