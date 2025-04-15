<template>
    <el-drawer v-model="drawer" size="40%" :destroy-on-close="true">
        <template #header>
            <h2 class="custom-title">题目内容详情</h2>
        </template>
        <el-form :model="formQuestion" ref="formRef">
            <el-form-item label="题⽬标题:">
                <el-input style="width:387px !important" v-model="formQuestion.title" placeholder="请输⼊标题"></el-input>
            </el-form-item>
            <el-form-item label="题⽬难度:">
                <QuestionSelector style="width:387px !important" v-model="formQuestion.difficulty" width="100%"
                    placeholder="请选择题⽬难度">
                </QuestionSelector>
            </el-form-item>
            <el-form-item label="时间限制（单位毫秒）:">
                <el-input style="width:300px !important" v-model="formQuestion.timeLimit"
                    placeholder="请输⼊时间限制"></el-input>
            </el-form-item>
            <el-form-item label="空间限制（单位字节）:">
                <el-input style="width:300px !important" v-model="formQuestion.spaceLimit"
                    placeholder="请输⼊空间限制"></el-input>
            </el-form-item>
            <el-form-item label="题⽬内容:">
                <div class="editor">
                    <quill-editor placeholder="请输⼊题⽬内容" v-model:content="formQuestion.content" content-type="html">
                    </quill-editor>
                </div>
            </el-form-item>
            <el-form-item label="题⽬⽤例:">
                <el-input style="width:387px !important" v-model="formQuestion.questionCase"
                    placeholder="请输⼊题⽬⽤例"></el-input>
            </el-form-item>
            <el-form-item label="默认代码块:">
                <code-edit ref="showCodeform" :defaultCode="formQuestion.defaultCode"
                    @update:value="handleEditorContent"></code-edit>
            </el-form-item>
            <el-form-item label="main函数:">
                <code-edit ref="showFuncform" :defaultCode="formQuestion.mainFunc"
                    @update:value="handleEditorMainFunc"></code-edit>
            </el-form-item>
            <el-form-item>
                <el-button class="question-button" type="primary" plain @Click="onSubmit()">发布</el-button>
            </el-form-item>
        </el-form>
    </el-drawer>
</template>

<script setup>
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import CodeEdit from './CodeEdit.vue';
import { ref, reactive } from 'vue';
import QuestionSelector from './QuestionSelector.vue';
import { ElMessage } from 'element-plus';
import { addQuestion, getQuestionDetail, editQuestionDetail } from '@/api/question';

const drawer = ref(false)
const formQuestion = reactive({
    questionId: '',
    title: '',
    difficulty: '',
    content: '',
    questionCase: '',
    timeLimit: '',
    spaceLimit: '',
    defaultCode: '',
    mainFunc: '',
})


const showCodeform = ref()
const showFuncform = ref()

// 再次打开后内容置空
async function open(questionId) {
    drawer.value = true
    for (const key in formQuestion) {
        if (formQuestion.hasOwnProperty(key)) {
            formQuestion[key] = ''
        }
    }
    if (questionId) {
        const questionInfo = await getQuestionDetail(questionId)
        Object.assign(formQuestion, questionInfo.data)
        showCodeform.value.showCode(formQuestion.defaultCode)
        showFuncform.value.showCode(formQuestion.mainFunc)
    }
}

defineExpose({
    open
})


function isValid() {
    let msg = ''
    if (!formQuestion.title) {
        msg = '请添加题目标题'
    } else if (formQuestion.difficulty == '') {
        msg = '请选择题目难度'
    } else if (!formQuestion.timeLimit) {
        msg = '请输入时间限制'
    } else if (!formQuestion.spaceLimit) {
        msg = '请输入空间限制'
    } else if (!formQuestion.content) {
        msg = '请输入题目内容'
    } else if (!formQuestion.questionCase) {
        msg = '请输入题目用例'
    } else if (!formQuestion.defaultCode) {
        msg = '请输入默认代码'
    } else if (!formQuestion.mainFunc) {
        msg = '请输入mian函数'
    } else {
        msg = ''
    }
    return msg
}


const event = defineEmits(['success'])
async function onSubmit() {
    const errorMsg = isValid()
    if (errorMsg) {
        ElMessage.error(errorMsg)
        return false
    }
    const questionData = new FormData()
    for (let key in formQuestion) {
        questionData.append(key, formQuestion[key])
    }
    if (formQuestion.questionId) {
        await editQuestionDetail(questionData)
        ElMessage.success('修改成功')
        event('success', 'edit')
    } else {
        await addQuestion(questionData)
        ElMessage.success('添加成功')
        event('success', 'add')
    }
    drawer.value = false
}

function handleEditorContent(content) {
    formQuestion.defaultCode = content
}

function handleEditorMainFunc(content) {
    formQuestion.mainFunc = content
}

</script>

<style lang="scss">
.question-button {
    width: 200px;
}

.custom-title {
    color: #000;
    font-size: 20px;
    font-weight: bold;
    margin: 0;

}

::v-deep(.el-drawer__header) {
    padding: 8px 10px;
    min-height: auto;
    margin-bottom: 0;
}
</style>
