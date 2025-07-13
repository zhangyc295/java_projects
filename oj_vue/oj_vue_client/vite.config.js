import { fileURLToPath, URL } from "node:url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools";



// 👇 引入 AutoImport、Components 和 ElementPlusResolver
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  // 修改端口号
  // server: {
  //   port: 19090
  // }
  server: {
    proxy: {
      "/dev-api": {
        target: "http://127.0.0.1:19090/friend",
        rewrite: (p) => p.replace(/^\/dev-api/, ""),
      }, // 请求转发
    },
  },
});

