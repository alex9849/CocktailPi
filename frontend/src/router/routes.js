import store from '../store/index';

function redirectIfNotAuthenticated(to, from, next) {
  if(!store().getters['auth/isLoggedIn']) {
    next({name: "login"})
  }
  next();
}

function redirectIfAuthenticated(to, from, next) {
  if(store().getters['auth/isLoggedIn']) {
    next({name: "mycocktails"})
  }
  next();
}

const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    redirect: {name: 'login'},
    children: [
      {
        path: '',
        component: () => import('pages/Index.vue')
      }, {
        path: 'MyCocktails',
        component: () => import('pages/MyCocktails'),
        name: "mycocktails",
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
