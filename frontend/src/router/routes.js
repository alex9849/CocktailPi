import store from '../store/index';

function redirectIfNotAuthenticated(to, from, next) {
  if(!store().getters['auth/isLoggedIn']) {
    next({name: "login"})
  }
  next();
}

function redirectIfAuthenticated(to, from, next) {
  if(store().getters['auth/isLoggedIn']) {
    next({name: "dashboard"})
  }
  next();
}

const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    redirect: {name: 'login'},
    children: [{
        path: 'Dashboard',
        component: () => import('pages/Dashboard'),
        name: "dashboard",
        beforeEnter: redirectIfNotAuthenticated
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
