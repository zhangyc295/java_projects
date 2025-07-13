import { getToken } from '@/utils/cookie'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/oj/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
    },
    
    {
      path: '/',
      redirect:'/oj/login'
    },   //http://localhost:5173/  ->  http://localhost:5173/oj/system

    

    {
      path: '/oj/system',
      name: 'system',
      component: () => import('../views/System.vue'),
      children: [
        {
          path: 'user',
          name: 'user',
          component: () => import('../views/User.vue'),
        },
        {
          path: 'question',
          name: 'question',
          component: () => import('../views/Question.vue'),
        },
        {
          path: 'contest',
          name: 'contest',
          component: () => import('../views/Contest.vue'),
        },
        {
          path: 'addContest',
          name: 'addContest',
          component: () => import('../views/AddContest.vue'),
        }
      ]
    },
  ],
})


// 路由守卫
router.beforeEach((to, from, next) =>{
  if(getToken()){
    if(to.path === '/oj/login'){
      next({ path: '/oj/system/question'})
    }else{
      next()
    }
  } else {
    if(to.path !== '/oj/login')  {
      next({ path: '/oj/login'})
    }else{
      next()
    }
  }
})

export default router
