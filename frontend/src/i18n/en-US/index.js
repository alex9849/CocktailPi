export default {
  header: {
    'machine-name': 'CocktailMaker',
    profile: {
      'profile-btn-label': 'Profile',
      'logout-btn-label': 'Logout'
    }
  },
  'full-layout': {
    sidebar: {
      me: {
        label: 'Me',
        dashboard: 'Dashboard',
        'simple-view': 'Simple view',
        bar: 'Bar',
        collections: 'Collections',
        'my-recipes': 'My recipes'
      },
      cocktails: {
        label: 'Cocktails',
        'category-all': 'All',
        ingredients: 'Ingredients'
      },
      administration: {
        label: 'Administration',
        'user-mgmt': 'Users',
        'ingredient-mgmt': 'Ingredients',
        'category-mgmt': 'Categories',
        'gpio-mgmt': 'GPIO',
        'pump-mgmt': 'Pumps',
        'glass-mgmt': 'Glasses',
        'event-mgmt': 'Events',
        'system-mgmt': 'System'
      }
    },
    'donate-btn-label': 'Donate'
  },
  'login-page': {
    'login-btn-label': 'Login',
    headline: 'Login',
    'password-field-label': 'Password',
    'username-field-label': 'Username',
    'remember-me-label': 'Remember me'
  },
  bar_page: {
    headline: 'Ingredients owned',
    add_btn_label: 'Add ingredient',
    refresh_btn_label: 'Refresh',
    owned_table: {
      nr_ingredients_owned: '{nrIngredients} ingredient(s) in total',
      no_data_msg: 'No ingredients added',
      columns: {
        ingredient: 'Ingredient',
        alc_content: 'Alcohol content',
        actions: 'Actions'
      },
      delete_btn_tooltip: 'Delete'
    },
    add_dialog: {
      headline: 'Add Ingredient',
      ingredient_selector_label: 'Ingredient',
      abort_btn_label: 'Abort',
      save_btn_label: 'Save',
      required_error: 'Required'
    },
    notifications: {
      ingredient_added: 'Ingredient added successfully',
      ingredient_removed: 'Ingredient removed successfully'
    }
  },
  404: {
    'go-home-btn-label': 'Go Home',
    message: 'Oops. Nothing here...'
  }
}
