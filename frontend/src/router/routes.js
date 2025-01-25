import store from '../store'

function redirectIfNotAuthenticated (to, from, next) {
  if (!store().getters['auth/isLoggedIn']) {
    const query = {
      redirectTo: to.fullPath
    }
    if (!query.redirectTo || query.redirectTo === '/') {
      delete query.redirectTo
    }
    next({
      name: 'login',
      query
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
    component: () => import('layouts/RootLayout.vue'),
    children: [
      {
        path: '/',
        component: () => import('layouts/LoggedInLayout.vue'),
        beforeEnter: redirectIfNotAuthenticated,
        children: [{
          path: '/',
          component: () => import('layouts/FullLayout.vue'),
          children: [{
            path: '',
            redirect: { name: 'dashboard' }
          }, {
            path: 'dashboard',
            component: () => import('pages/Dashboard'),
            name: 'dashboard'
          }, {
            path: 'bar',
            component: () => import('pages/Bar'),
            name: 'bar'
          }, {
            path: 'collection/own',
            component: () => import('pages/Collections'),
            name: 'collections'
          }, {
            path: 'collection/:id',
            component: () => import('pages/Collection'),
            name: 'collection'
          }, {
            path: 'user/profile',
            component: () => import('pages/UserEditor'),
            name: 'myprofile'
          }, {
            path: 'recipe/add',
            component: () => import('pages/RecipeEdit'),
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
            path: 'recipe/ingredients',
            component: () => import('pages/IngredientRecipes'),
            name: 'ingredientrecipes'
          }, {
            path: 'recipe/public/category/:cid',
            component: () => import('pages/RecipesPublic'),
            name: 'publiccategoryrecipes'
          }, {
            path: 'recipe/:id',
            component: () => import('pages/RecipeDetails'),
            name: 'recipedetails'
          }, {
            path: 'recipe/:id/order',
            component: () => import('pages/RecipeDetails'),
            name: 'recipeorder'
          }, {
            path: 'recipe/:id/edit',
            component: () => import('pages/RecipeEdit'),
            name: 'recipeedit'
          }, {
            path: 'admin/ingredient',
            component: () => import('pages/IngredientManagement'),
            name: 'ingredientmanagement'
          }, {
            path: 'admin/user',
            component: () => import('pages/UserManagement'),
            name: 'usermanagement'
          }, {
            path: 'admin/user/create',
            component: () => import('pages/UserEditor'),
            name: 'usercreator'
          }, {
            path: 'admin/user/:userId/edit',
            component: () => import('pages/UserEditor'),
            name: 'usereditor'
          }, {
            path: 'admin/pump',
            component: () => import('pages/PumpManagement'),
            name: 'pumpmanagement'
          }, {
            path: 'admin/pump/settings/reversepumping',
            component: () => import('pages/ReversePumpSettings.vue'),
            name: 'reversepumpsettings'
          }, {
            path: 'admin/pump/settings/loadcell',
            component: () => import('pages/LoadCellSettings.vue'),
            name: 'loadcellsettings'
          }, {
            path: 'admin/pump/settings/powerlimit',
            component: () => import('pages/PowerLimitSettings.vue'),
            name: 'powerlimitsettings'
          }, {
            path: 'admin/pump/:pumpId/edit',
            component: () => import('pages/SetupPump'),
            name: 'editpump'
          }, {
            path: 'admin/category',
            component: () => import('pages/CategoryManagement'),
            name: 'categorymanagement'
          }, {
            path: 'admin/event',
            component: () => import('pages/EventManagement'),
            name: 'eventmanagement'
          }, {
            path: 'admin/gpio',
            component: () => import('pages/GpioManagement'),
            name: 'gpiomanagement'
          }, {
            path: 'admin/gpio/i2c',
            component: () => import('pages/I2CManagement'),
            name: 'i2cmanagement'
          }, {
            path: 'admin/gpio/expander/new',
            component: () => import('pages/GpioExpanderEdit'),
            name: 'gpioexpanderadd'
          }, {
            path: 'admin/gpio/expander/:id/edit',
            component: () => import('pages/GpioExpanderEdit'),
            name: 'gpioexpandereditor'
          }, {
            path: 'admin/system',
            component: () => import('pages/SystemManagement'),
            name: 'systemmanagement'
          }, {
            path: 'admin/glass',
            component: () => import('pages/GlassManagement'),
            name: 'glassmanagement'
          }
          ]
        }, {
          path: '/simple',
          component: () => import('layouts/SimpleTouchLayout.vue'),
          children: [{
            path: 'collection',
            component: () => import('pages/SimpleCollections.vue'),
            name: 'simplecollections'
          }, {
            path: 'collection/:collectionId',
            component: () => import('pages/SimpleCollection.vue'),
            name: 'simplecollection'
          }, {
            path: 'recipe',
            component: () => import('pages/SimpleRecipes.vue'),
            name: 'simplerecipes'
          }, {
            path: 'recipe/ingredients',
            component: () => import('pages/SimpleIngredientRecipes.vue'),
            name: 'simpleingredientrecipes'
          }, {
            path: 'orderprogress',
            component: () => import('pages/SimpleOrderProgress.vue'),
            name: 'simpleorderprogress'
          }]
        }]
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
        path: ':catchAll(.*)*',
        name: '404Page',
        component: () => import('pages/Error404.vue')
      }
    ]
  }
]

export default routes
