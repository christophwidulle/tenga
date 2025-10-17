import { createRouter, createWebHistory } from 'vue-router'
import { api } from '@/services/api'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/documents/:id',
      name: 'document',
      component: () => import('@/views/DocumentView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/documents/:id/edit',
      name: 'document-edit',
      component: () => import('@/views/DocumentEditView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/documents/new',
      name: 'document-new',
      component: () => import('@/views/DocumentEditView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/documents/:id/versions',
      name: 'document-versions',
      component: () => import('@/views/VersionHistoryView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !api.hasApiKey()) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
