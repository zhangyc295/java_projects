<template>
    <!-- <el-select v-model="laguage" placeholder="java" style="width: 100px">
        <el-option v-for="item in laguages" :key="item" :label="item" :value="item"/>
    </el-select> -->
    <div ref="editorform" class="ace-editor">
    </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount} from "vue";
import ace from "ace-builds";
import "ace-builds/src-noconflict/mode-java"
import "ace-builds/src-noconflict/theme-eclipse"
import "ace-builds/src-noconflict/ext-language_tools";


// 创建响应式引用
const editorform = ref(null);
let editor = null;
const emit = defineEmits(['update:value']);

const options = {
    theme: `ace/theme/eclipse`,
    mode: `ace/mode/java`,
    maxLines: 25,
    minLines: 25,
    fontSize: 15,
};

// 初始化编辑器
onMounted(() => {
    editor = ace.edit(editorform.value, options);
    editor.setOptions({
        enableBasicAutocompletion: true,
    });
    editor.getSession().on('change', () => {
        // 当编辑器内容变化时，触发自定义事件并传递编辑器的内容
        emit('update:value', editor.getValue());
    });
});

// 销毁编辑器实例
onBeforeUnmount(() => {
    if (editor) {
        editor.destroy();
        editor = null;
    }
});

function setAceCode(content) {
    editor.setValue(content)
}

defineExpose({
    setAceCode
})
</script>

<style lang="scss" scoped>
    .ace-editor {
        height: 304px;
    }
</style>