import store from '../store/index';

function redirectIfNotAuthenticated(to, from, next) {
  if (!store().getters['auth/isLoggedIn']) {
    next({name: "login"})
  }
  next();
}

function redirectIfAuthenticated(to, from, next) {
  if (store().getters['auth/isLoggedIn']) {
    next({name: "dashboard"})
  }
  next();
}

const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    redirect: {name: 'login'},
    beforeEnter: redirectIfNotAuthenticated,
    children: [{
      path: 'dashboard',
      component: () => import('pages/Dashboard'),
      name: "dashboard"
    }, {
      path: 'user/recipes',
      component: () => import('pages/MyRecipes'),
      name: 'myrecipes'
    }, {
      path: 'user/profile',
      component: () => import('pages/Profile'),
      name: 'myprofile'
    }, {
      path: 'public/recipes',
      component: () => import('pages/PublicRecipes'),
      name: 'publicrecipes'
    }, {
      path: 'admin/usermanagement',
      component: () => import('pages/UserManagement'),
      name: 'usermanagement'
    }, {
      path: 'admin/settings',
      component: () => import('pages/Settings'),
      name: 'adminsettings'
    }
    ]
  }, {
    path: '/login',
    component: () => import('layouts/EmptyLayout'),
    children: [
      {
        path: '',
        name: 'login',
        component: () => import('pages/Login'),
        beforeEnter: redirectIfAuthenticated
      }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '*',
    component: () => import('pages/Error404.vue')
  }
];

export default routes
