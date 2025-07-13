<template>
    <div class="oj-main-layout">
        <div class="oj-main-layout-header">
            <div class="oj-main-layout-nav">
                <Navbar></Navbar>
            </div>
        </div>
        <div v-if="$route.meta.showBanner">
            <img src="@/assets/images/log-banner.png" class="banner-img">
        </div>
    </div>
    <RouterView />
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'
</script>

<style lang="scss" scoped>
.el-main {
    padding: 0;
}

.oj-main-layout {
    // background-color: #f7f7f7;
    padding-top: 20px;

    .banner-img {
        max-width: 1520px;
        margin: 0 auto;
        border-radius: 16px;
        width: "100%";
        // height: 200px; 
        // object-fit: cover; 
        // 防止图片变形（可选）
    }

    .oj-main-layout-header {
        height: 60px;
        position: absolute;
        width: 100%;
        background: #fff;
        left: 0;
        top: 0;
        z-index: 3;
        overflow: hidden;
    }

    .oj-main-layout-nav {
        max-width: 1520px;
        min-width: 100%;
        margin: 0 auto;
        height: 60px;
        background: #fff;
    }

    // banner 图
    .oj-ship-banner {
        max-width: 1520px;
        min-width: 1520;
        margin: 0 auto;
        width: 100%;
        height: 100%;
        height: 350px;
        // width: 1677px;
        color: #ffffff;
        // background: url("@/assets/index_bg.png") left top no-repeat;
        background-size: cover;
        overflow: hidden;
    }
}
</style>



<!-- <template>
    首页界面
</template> -->