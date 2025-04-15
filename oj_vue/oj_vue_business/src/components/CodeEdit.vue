<template>
    <div ref="editorform" class="ace-editor">
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue"
import ace from "ace-builds"
import "ace-builds/src-noconflict/mode-java"
import "ace-builds/src-noconflict/theme-eclipse"
import "ace-builds/src-noconflict/ext-language_tools";

// 定义选项
const options = {
    theme: `ace/theme/eclipse`,
    mode: `ace/mode/java`,
    maxLines: 20,
    minLines: 10,
    fontSize: 15,
};
// 创建响应式引⽤
let editor = null;
const emit = defineEmits(['update:value']);
const editorform = ref(null);
// 初始化编辑器
onMounted(() => {
    editor = ace.edit(editorform.value, options);
    editor.setOptions({
        enableBasicAutocompletion: true,
    });
    editor.getSession().on('change', () => {
        // 当编辑器内容变化时，触发⾃定义事件并传递编辑器的内容
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

function showCode(code) {
    editor.setValue(code)
}

defineExpose({
    showCode
})

</script>

<style lang="scss" scoped>
.ace-editor {
    margin: 10px 0 0 0;
    width: 90%;
}
</style>