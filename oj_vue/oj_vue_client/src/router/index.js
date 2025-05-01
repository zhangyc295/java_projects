import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/client-oj/home",
      name: "home",
      component: () => import("../views/Home.vue"),
      children: [
        {
          path: "question",
          name: "question",
          component: () => import("../views/Question.vue"),
          meta: { showBanner: true },
        },
        {
          path: "contest",
          name: "contest",
          component: () => import("../views/Contest.vue"),
          meta: { showBanner: true },
        },
        {
          path: "client/contest",
          name: "clientContest",
          component: () => import("../views/ClientContest.vue"),
          meta: { showBanner: false },
        },
        {
          path: "client/message",
          name: "clientMessage",
          component: () => import("../views/ClientMessage.vue"),
          meta: { showBanner: false },
        },
        {
          path: "client/detail",
          name: "ClientDetail",
          component: () => import("../views/ClientDetail.vue"),
          meta: { showBanner: false },
        },

        {
          path: "client/password",
          name: "ClientPassword",
          component: () => import("../views/ClientPassword.vue"),
          meta: { showBanner: false },
        },
      ],
    },

    {
      path: "/",
      redirect: "/client-oj/home/question",
    },

    {
      path: "/client-oj/login",
      name: "/login",
      component: () => import("../views/Login.vue"),
    },

    {
      path: "/client-oj/answer",
      name: "/answer",
      component: () => import("../views/Answer.vue"),
    },
  ],
});

export default router;
