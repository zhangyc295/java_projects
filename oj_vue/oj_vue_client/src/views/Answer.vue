<template>
    <div class="page praticle-page flex-col">
        <div class="box_1 flex-row">
            <div class="group_1 ">
                <img class="label_4" src="@/assets/ide/liebiao.png" />
                <span>{{ title ? title : "默认题库" }}</span>
                <!-- <span>{{  "默认题库" }}</span> -->
                <el-countdown v-if="endTime && new Date() < new Date(endTime)" class="exam-time-countdown"
                    @finish="handleCountdownFinish" title="距离竞赛结束还有:" :value="new Date(endTime)" />
            </div>
            <div class="group_2">
                <el-button type="primary" plain @click="submitQuestion">提交代码</el-button>
            </div>
            <span class="ide-back" @click="goBack()">返回</span>
        </div>
        <div class="box_8 flex-col">
            <div class="group_12 flex-row justify-between">
                <div class="image-wrapper_1 flex-row">
                    <img class="thumbnail_2" src="@/assets/ide/xiaobiaoti.png" />
                    <div class="question-nav">
                        <span>题目描述</span>
                    </div>
                    <div class="question-nav" @click="preQuestion">
                        <el-icon>
                            <span>上一题</span>
                            <ArrowLeft />
                        </el-icon>
                    </div>
                    <div class="question-nav" @click="nextQuestion">
                        <el-icon>
                            <ArrowRight />
                            <span>下一题</span>
                        </el-icon>
                    </div>
                </div>
                <div class="image-wrapper_2 flex-row">
                    <img class="image_1" src="@/assets/ide/daima.png" />
                    <span style="margin-right: 400px;">代码</span>
                    <el-select v-model="languages" placeholder="java" style="width: 100px">
                        <el-option v-for="item in languages" :key="item" :label="item" :value="item" />
                    </el-select>
                </div>
            </div>
            <div class="group_13 flex-row justify-between">
                <div class="box_3 flex-col">
                    <span class="question-title">{{ questionDetail.title }}</span>
                    <span class="question-limit">
                        <div v-if="questionDetail.difficulty === 1">题目难度：简单 时间限制：{{ questionDetail.timeLimit }} ms
                            空间限制：{{
                                questionDetail.spaceLimit }} 字节</div>
                        <div v-if="questionDetail.difficulty === 2">题目难度：中等 时间限制：{{ questionDetail.timeLimit }} ms
                            空间限制：{{
                                questionDetail.spaceLimit }} 字节</div>
                        <div v-if="questionDetail.difficulty === 3">题目难度：困难 时间限制：{{ questionDetail.timeLimit }} ms
                            空间限制：{{
                                questionDetail.spaceLimit }} 字节</div>
                    </span>
                    <span class="question-content" v-html="questionDetail.content"></span>
                </div>
                <div class="group_14 flex-col">
                    <div class="group_8 flex-col">
                        <codeEditor ref="defaultCodeRef" @update:value="handleEditorContent">
                        </codeEditor>
                    </div>
                    <div class="code-result flex-row">
                        <img class="code-result-image" src="@/assets/ide/codeResult.png" />
                        <span class="code-result-content">执行结果</span>
                    </div>
                    <div class="group_15 flex-row">
                        <div class="section_1 flex-row">
                            <div class="section_3 flex-col">
                                <div class="text-wrapper_2 flex-row justify-between">
                                    <span class="text_1 red" v-if="submitResultVO.pass === 0">未通过</span>
                                    <span class="text_1 success" v-if="submitResultVO.pass === 1">通过</span>
                                    <span class="text_1 info" v-if="submitResultVO.pass === 2">请先执行代码</span>
                                    <span class="text_1 warning" v-if="submitResultVO.pass === 3">系统正在处理您的代码，请稍后</span>
                                </div>
                                <span class="error-text" v-if="submitResultVO.pass === 0">异常信息：{{
                                    submitResultVO.errorMessage }}</span>
                                <el-table
                                    v-if="submitResultVO.submitResultDetail && submitResultVO.submitResultDetail.length > 0"
                                    :data="submitResultVO.submitResultDetail">
                                    <el-table-column prop="input" label="输入" />
                                    <el-table-column prop="output" label="预期结果" />
                                    <el-table-column prop="executeOutput" label="实际输出" />
                                </el-table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import codeEditor from "@/components/CodeEditor.vue"
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { useRoute } from "vue-router"
import { getQuestionDetailService, preQuestionService, nextQuestionService, getQuestionResultService } from "@/api/question"
import router from "@/router"
import { getContestFirstQuestionService, contestPrevQuestionService, contestNextQuestionService } from "@/api/contest"
import { ElMessage } from "element-plus"
import { userSubmitService } from "@/api/user"

const languages = ref([
    "java"
])

function goBack() {
    router.go(-1);
}
const questionDetail = reactive({})
const defaultCodeRef = ref()

let questionId = useRoute().query.questionId
let contestId = useRoute().query.contestId
let title = useRoute().query.title
let endTime = useRoute().query.endTime

// console.log('contestTitle: ', title)

async function getQuestionDetail() {
    if (contestId && (questionId == null || questionId == '')) {
        const eqrs = await getContestFirstQuestionService(contestId)
        questionId = eqrs.data
        console.log('qId: ', questionId)
    }
    const res = await getQuestionDetailService(questionId)
    Object.assign(questionDetail, res.data)
    defaultCodeRef.value.setAceCode(questionDetail.defaultCode)
}
getQuestionDetail()

async function preQuestion() {
    if (contestId) {
        //竞赛中上一题的逻辑  需要提供一个竞赛中获取上一题的接口
        const res = await contestPrevQuestionService(contestId, questionId)
        questionId = res.data
    } else {
        const res = await preQuestionService(questionId)
        questionId = res.data
    }
    getQuestionDetail()
}

async function nextQuestion() {
    if (contestId) {
        //竞赛中下一题的逻辑 需要提供一个竞赛中获取下一题的接口
        const res = await contestNextQuestionService(contestId, questionId)
        questionId = res.data
    } else {
        const res = await nextQuestionService(questionId)
        questionId = res.data
    }
    getQuestionDetail()
}

function handleCountdownFinish() {
    ElMessage.info('竞赛已经结束')
    router.push('/client-oj/home/contest')
}

const submitDTO = reactive({
    contestId: '',
    questionId: '',
    programLanguage: 0,
    submitCode: ''
})

function handleEditorContent(content) {
    submitDTO.submitCode = content
}

const submitResultVO = ref({
    pass: 2,  //默认值为2，未提交代码
    errorMessage: '',
    submitResultDetail: [],
})

const pollingInterval = ref(null);
let submitTime

function startPolling() {
    stopPolling(); // 停止之前的轮询
    pollingInterval.value = setInterval(() => {
        getQuestionResult();
    }, 2000); // 每隔2秒请求一次
}

function stopPolling() {
    if (pollingInterval.value) {
        clearInterval(pollingInterval.value);
        pollingInterval.value = null;
    }
}

async function submitQuestion() {
    submitDTO.contestId = contestId
    submitDTO.questionId = questionId
    await userSubmitService(submitDTO)
    submitTime = new Date().toLocaleString();
    submitResultVO.value.pass = 3
    startPolling()
}

async function getQuestionResult() {
    const res = await getQuestionResultService(submitDTO.questionId, submitDTO.contestId, submitTime)
    submitResultVO.value = res.data
    if (submitResultVO.value.pass !== 3) {
        stopPolling();
    }
}

</script>

<style lang="scss" scoped>
.praticle-page {
    margin-top: -40px;

    .box_1 {
        background-color: rgba(240, 240, 240, 1);
        height: 60px;
        display: flex;
        align-items: center;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 20px;

        /* 垂直居中 */

        .box_1 span {
            margin-right: 10px;
            /* 文字与图片之间的间距 */
        }

        .exam-time-countdown {
            margin: 0 0 0 400px;
        }

        .group_1 {
            display: flex;
            align-items: center;

            img {
                margin-right: 10px;
            }
        }

        .thumbnail_1 {
            width: 1px;
            height: 20px;
            margin: 20px 0 0 8px;
        }

        .label_4 {
            width: 26px;
            height: 26px;
        }

        .group_2 {}

        .group_3 {
            cursor: pointer;
            background-color: rgba(7, 126, 255, 0.1);
            border-radius: 0px 6px 6px 0px;
            width: 100px;
            height: 40px;
            margin: 10px 0 0 2px;
            font-size: 20px;

            display: flex;
            justify-content: center;
            align-items: center;
        }

        .ide-back {
            cursor: pointer;
            color: #999999;
        }

        .label_2 {
            width: 30px;
            height: 30px;
            margin: 15px 0 0 500px;
        }

        .label_3 {
            width: 30px;
            height: 30px;
            margin: 15px 70px 0 30px;
        }
    }
}

.page {
    background-color: rgba(247, 247, 247, 1);
    position: relative;
    height: 910px;
    overflow: hidden;

    .box_8 {
        position: relative;
        width: 100%;
        height: 1451px;
        margin-bottom: 1px;

        .group_12 {
            width: 100%;
            height: 64px;
            margin-top: 10px;

            .image-wrapper_1 {
                background-color: rgba(240, 240, 240, 1);
                border-radius: 10px 10px 0px 0px;
                height: 64px;
                width: 600px;
                margin: 0 10px 10px 0;

                display: flex;
                align-items: center;
                /* 垂直居中 */

                .thumbnail_2 {
                    width: 14px;
                    height: 15px;
                    margin: 0 10px 0 19px;
                }

                .question-nav {
                    cursor: pointer;
                    margin-right: 100px;
                    display: flex;
                    align-items: center;
                }
            }

            .image-wrapper_2 {
                background-color: rgba(240, 240, 240, 1);
                border-radius: 10px 10px 0px 0px;
                height: 64px;
                display: flex;
                align-items: center;
                flex: 1;
                /* 垂直居中 */

                .image_1 {
                    width: 21px;
                    height: 16px;
                    margin: 0 10px 0 19px;
                }
            }
        }

        .group_13 {
            height: 904px;

            .box_3 {
                background-color: rgba(255, 255, 255, 1);
                border-radius: 10px;
                height: 765px;
                width: 600px;
                margin-right: 10px;

                .question-title {
                    font-weight: bold;
                    font-size: 28px;
                    margin-top: 20px;
                    margin-left: 20px;
                    margin-bottom: 10px;
                }

                .question-limit {
                    font-size: 18px;
                    color: #abaeac;
                    margin-left: 20px;
                    margin-bottom: 10px;
                }

                .question-content {
                    font-size: 20px;
                    margin-left: 20px;
                }

                .group_6 {
                    background-color: rgba(241, 241, 241, 1);
                    width: 21px;
                    height: 874px;
                    justify-content: flex-center;
                    margin: 10px 0 0 923px;

                    .thumbnail_3 {
                        width: 9px;
                        height: 6px;
                        margin: 10px 0 0 6px;
                    }

                    .group_7 {
                        background-color: rgba(192, 192, 192, 1);
                        width: 16px;
                        height: 296px;
                        margin: 10px 0 0 3px;
                    }

                    .thumbnail_4 {
                        width: 9px;
                        height: 6px;
                        margin: 536px 0 10px 6px;
                    }
                }
            }

            .group_14 {
                height: 904px;
                display: flex;
                width: calc(100vw - 663px);

                .group_8 {
                    background-color: rgba(255, 255, 255, 1);
                    border-radius: 10px;
                    flex: 1;

                    .thumbnail_5 {
                        width: 11px;
                        height: 6px;
                        margin: 31px 0 0 69px;
                    }

                    .block_1 {
                        width: 21px;
                        height: 710px;
                        border: 1px solid rgba(241, 241, 241, 1);
                        margin: 19px 0 0 923px;
                    }

                    .image_2 {
                        width: calc(100vw - 663px);
                        height: 1px;
                        margin: -710px 0 763px 0;
                    }
                }

                .code-result {
                    background-color: rgba(240, 240, 240, 1);
                    border-radius: 10px 10px 0px 0px;
                    height: 80px;
                    margin-top: 20px;
                    width: calc(100vw - 663px);

                    .code-result-image {
                        width: 27px;
                        height: 27px;
                        margin: 24px 0 0 20px;
                    }

                    .code-result-content {
                        margin: 26px 0 0 5px;
                    }
                }
            }
        }

        .group_15 {
            width: calc(100vw - 663px);
            height: 452px;

            .section_1 {
                background-color: rgba(255, 255, 255, 1);
                border-radius: 0px 0px 10px 10px;
                width: 100%;
                height: 240px;

                .section_3 {
                    width: 100%;
                    height: 286px;
                    margin: 24px 0 0 20px;

                    .error-text {
                        padding: 6px 20px;
                        font-size: 14px;
                        color: #666;
                        background: #f7f7f7;
                        border-left: 2px solid red;
                        width: 95%;
                    }

                    .text-wrapper_2 {
                        width: 217px;
                        height: 40px;

                        .text_1 {
                            width: 60px;
                            height: 40px;
                            overflow-wrap: break-word;
                            color: rgba(7, 126, 255, 1);
                            font-size: 20px;
                            font-family: MicrosoftYaHei;
                            font-weight: normal;
                            text-align: left;
                            white-space: nowrap;
                            line-height: 40px;

                            &.red {
                                color: red;
                            }

                            &.info {
                                color: #32C5FF;
                            }

                            &.success {
                                color: green;
                            }

                            &.warning {
                                color: orange;
                            }
                        }

                        .text_2 {
                            width: 137px;
                            height: 24px;
                            overflow-wrap: break-word;
                            color: rgba(153, 153, 153, 1);
                            font-size: 18px;
                            font-family: MicrosoftYaHei;
                            font-weight: normal;
                            text-align: left;
                            white-space: nowrap;
                            line-height: 24px;
                            margin-top: 13px;
                        }
                    }

                    .box_9 {
                        width: 382px;
                        height: 50px;
                        margin-top: 10px;

                        .box_5 {
                            background-color: rgba(242, 243, 245, 1);
                            border-radius: 8px;
                            width: 100px;
                            height: 40px;

                            .section_2 {
                                background-color: rgba(7, 126, 255, 1);
                                border-radius: 50%;
                                width: 5px;
                                height: 5px;
                                margin: 23px 0 0 24px;
                            }

                            .text_3 {
                                width: 63px;
                                height: 26px;
                                overflow-wrap: break-word;
                                color: rgba(34, 34, 34, 1);
                                font-size: 15px;
                                font-family: MicrosoftYaHei;
                                font-weight: normal;
                                text-align: right;
                                white-space: nowrap;
                                line-height: 26px;
                                margin: 10px 4px 0 0;
                            }
                        }

                        .box_6 {
                            background-color: rgba(153, 153, 153, 1);
                            border-radius: 50%;
                            width: 5px;
                            height: 5px;
                            margin: 23px 0 0 44px;
                        }

                        .text_4 {
                            width: 63px;
                            height: 26px;
                            overflow-wrap: break-word;
                            color: rgba(153, 153, 153, 1);
                            font-size: 20px;
                            font-family: MicrosoftYaHei;
                            font-weight: normal;
                            text-align: right;
                            white-space: nowrap;
                            line-height: 26px;
                            margin: 12px 0 0 6px;
                        }

                        .box_7 {
                            background-color: rgba(153, 153, 153, 1);
                            border-radius: 50%;
                            width: 5px;
                            height: 5px;
                            margin: 23px 0 0 68px;
                        }

                        .text_5 {
                            width: 63px;
                            height: 26px;
                            overflow-wrap: break-word;
                            color: rgba(153, 153, 153, 1);
                            font-size: 20px;
                            font-family: MicrosoftYaHei;
                            font-weight: normal;
                            text-align: right;
                            white-space: nowrap;
                            line-height: 26px;
                            margin: 12px 0 0 6px;
                        }
                    }

                    .text_6 {
                        width: 40px;
                        height: 26px;
                        overflow-wrap: break-word;
                        color: rgba(102, 102, 102, 1);
                        font-size: 20px;
                        font-family: MicrosoftYaHei;
                        font-weight: normal;
                        text-align: right;
                        white-space: nowrap;
                        line-height: 26px;
                        margin-top: 24px;
                    }

                    .block_4 {
                        background-color: rgba(242, 243, 245, 1);
                        border-radius: 8px;
                        width: 883px;
                        height: 106px;
                        margin-top: 16px;
                    }
                }

                .block_5 {
                    background-color: rgba(241, 241, 241, 1);
                    width: 20px;
                    height: 422px;
                    justify-content: flex-center;
                    margin: 10px 1px 0 20px;

                    .thumbnail_7 {
                        width: 9px;
                        height: 6px;
                        margin: 10px 0 0 6px;
                    }

                    .group_10 {
                        background-color: rgba(192, 192, 192, 1);
                        width: 16px;
                        height: 296px;
                        margin: 10px 0 0 3px;
                    }

                    .thumbnail_8 {
                        width: 9px;
                        height: 6px;
                        margin: 84px 0 10px 6px;
                    }
                }
            }
        }

        .group_11 {
            box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.06);
            background-color: rgba(255, 255, 255, 1);
            border-radius: 10px;
            position: absolute;
            left: 82px;
            top: -9px;
            width: 284px;
            height: 218px;
        }
    }
}

.dialog-footer {
    display: block;
    text-align: right;
    /* 确保内容右对齐 */
    margin-top: 16px;

    .dialog-button {
        width: 150px;
    }
}
</style>