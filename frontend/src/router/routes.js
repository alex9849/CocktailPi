import {store} from '../store'


function redirectIfNotAuthenticated(to, from, next) {
  if (!store.getters['auth/isLoggedIn']) {
    next({name: "login", query: {
      redirectTo: to.fullPath
      }});
    return;
  }
  next();
}

function redirectIfAuthenticated(to, from, next) {
  if (store.getters['auth/isLoggedIn']) {
    next({name: "dashboard"});
    return;
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
      path: 'user/profile',
      component: () => import('pages/Profile'),
      name: 'myprofile'
    }, {
      path: 'recipe/add',
      component: () => import('pages/RecipeAdd'),
      name: 'recipeadd'
    }, {
      path: 'recipe/own',
      component: () => import('pages/RecipesOwn'),
      name: 'myrecipes'
    }, {
      path: 'recipe/public',
      component: () => import('pages/RecipesPublic'),
      name: 'publicrecipes'
    }, {
      path: 'recipe/:id',
      component: () => import('pages/RecipeDetails'),
      name: 'recipedetails'
    }, {
      path: 'recipe/:id/edit',
      component: () => import('pages/RecipeEdit'),
      name: 'recipeedit'
    }, {
      path: 'admin/usermanagement',
      component: () => import('pages/UserManagement'),
      name: 'usermanagement'
    }, {
      path: 'admin/usermanagement/create',
      component: () => import('pages/UserCreator'),
      name: 'usercreator'
    }, {
      path: 'admin/usermanagement/:userId/edit',
      component: () => import('pages/UserEditor'),
      name: 'usereditor'
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
