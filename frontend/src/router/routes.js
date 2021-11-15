import store from '../store'
import Error404 from 'pages/Error404.vue'

function redirectIfNotAuthenticated (to, from, next) {
  if (!store().getters['auth/isLoggedIn']) {
    next({
      name: 'login',
      query: {
        redirectTo: to.fullPath
      }
    })
    return
  }
  next()
}

function redirectIfAuthenticated (to, from, next) {
  if (store().getters['auth/isLoggedIn']) {
    next({ name: 'dashboard' })
    return
  }
  next()
}

const routes = [
  {
    path: '/',
    component: () => import('layouts/LoggedInLayout.vue'),
    redirect: { name: 'login' },
    beforeEnter: redirectIfNotAuthenticated,
    children: [{
      path: 'dashboard',
      component: () => import('pages/Dashboard'),
      name: 'dashboard'
    }, {
      path: 'mybar',
      component: () => import('pages/MyBar'),
      name: 'mybar'
    }, {
      path: 'collection/own',
      component: () => import('pages/MyCollections'),
      name: 'mycollections'
    }, {
      path: 'collection/:id',
      component: () => import('pages/Collection'),
      name: 'collection'
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
      path: 'recipe/public/category/:cid',
      component: () => import('pages/RecipesPublic'),
      name: 'publiccategoryrecipes'
    }, {
      path: 'recipe/:id',
      component: () => import('pages/RecipeDetails'),
      name: 'recipedetails'
    }, {
      path: 'recipe/:id/edit',
      component: () => import('pages/RecipeEdit'),
      name: 'recipeedit'
    }, {
      path: 'admin/ingredient',
      component: () => import('pages/IngredientManagement'),
      name: 'ingredientmanagement'
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
      path: 'admin/pumpmanagement',
      component: () => import('pages/PumpManagement'),
      name: 'pumpmanagement'
    }, {
      path: 'admin/categorymanagement',
      component: () => import('pages/CategoryManagement'),
      name: 'categorymanagement'
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
    path: '/:catchAll(.*)*',
    name: '404Page',
    component: Error404
  }
]

export default routes
