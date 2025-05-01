<template>
    <div class="message-list">
      <div class="message-list-block">
        <div class="message-list-header">
          <span class="ms-title">我的消息</span>
          <span class="message-list-back" @click="goBack()">返回</span>
        </div>
        <div class="mesage-list-content" v-for="(item, index) in messageList" :key="index">
          <img src="@/assets/message/notice.png" width="50px" class="image" />
          <div class="message-content">
            <div class="title-box">
              <div class="title">
                {{ item.messageTitle }}
              </div>
            </div>
            <div class="content">{{ item.messageContent }}</div>
          </div>
          <el-button class="mesage-button" link type="primary" @click.stop="handlerDelete(item)">删除</el-button>
        </div>
        <div class="message-pagination">
          <!-- 增加分页展示器 -->
          <el-pagination background layout="total, sizes, prev, pager, next, jumper" :total="total"
            v-model:current-page="params.pageNumber" v-model:page-size="params.pageSize" :page-sizes="[5, 10, 20]"
            @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { getMessageListService } from "@/api/message"
  import router from "@/router"
  import { reactive, ref } from "vue"
  
  const messageList = ref([]) //消息列表
  
  const total = ref(0)
  const params = reactive({
    pageNumber: 1,
    pageSize: 10,
  })
  
  //消息列表
  async function getMessageList() {
    const ref = await getMessageListService(params)
    messageList.value = ref.rows
    total.value = ref.total
  }
  getMessageList()
  
  const goBack = () => {
    router.go(-1)
  }
  // 分页
  function handleSizeChange(newSize) {
    params.pageNumber = 1
    getMessageList()
  }
  
  function handleCurrentChange(newPage) {
    getMessageList()
  }
  </script>
  
  <style lang="scss" scoped>
  .message-list {
    background-color: rgba(247, 247, 247, 1);
    position: relative;
    overflow: hidden;
    display: flex;
    justify-content: center;
  
    .message-list-block {
      margin: 0 auto;
      margin-bottom: 1px;
      margin-top: 20px;
      width: 100%;
      max-width: 1520px;
  
      .message-pagination {
        margin: 20px 0 0 1000px;
      }
  
      .message-list-header {
        background-color: rgba(255, 255, 255, 1);
        border-radius: 10px;
        max-width: 1520px;
        width: 100%;
        padding: 0 20px;
        height: 60px;
        font-size: 25px;
        text-indent: 20px;
        padding-left: 0;
        display: flex;
        align-items: center;
        background: #fff;
        box-sizing: border-box;
  
        .ms-title {
          font-family: PingFangSC, PingFang SC;
          font-weight: 600;
          font-size: 18px;
          color: #222222;
          text-align: left;
        }
  
        .message-list-back {
          cursor: pointer;
          color: #999999;
          font-size: 15px;
          margin-left: auto;
        }
      }
  
      .mesage-list-content {
        box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.1);
        background-color: rgba(255, 255, 255, 1);
        border-radius: 10px;
        margin-top: 20px;
        max-width: 1520px;
        background: #FFFFFF;
        box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.1);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 110px;
        width: 100%;
        padding-left: 20px;
  
        .mesage-button {
          width: 84px;
          height: 44px;
          background: #EAF9FF;
          border-radius: 22px;
          margin-right: 20px;
        }
  
        .message-content {
          color: #333333;
          position: relative;
          left: 20px;
          width: calc(100% - 155px);
  
          .title-box {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
          }
  
          .title {
            font-weight: bold;
            font-size: 16px;
          }
        }
      }
    }
  }
  </style>