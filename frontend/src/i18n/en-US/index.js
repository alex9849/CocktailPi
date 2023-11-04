export default {
  errors: {
    field_required: 'Required',
    not_valid_url: 'Not a valid URL',
    min_letters: 'Min {nr} letters',
    max_letters: 'Max {nr} letters'
  },
  header: {
    machine_name: 'CocktailMaker',
    profile: {
      profile_btn_label: 'Profile',
      logout_btn_label: 'Logout'
    }
  },
  layout: {
    full_layout: {
      sidebar: {
        me: {
          label: 'Me',
          dashboard: 'Dashboard',
          simple_view: 'Simple view',
          bar: 'Bar',
          collections: 'Collections',
          my_recipes: 'My recipes'
        },
        cocktails: {
          label: 'Cocktails',
          category_all: 'All',
          ingredients: 'Ingredients'
        },
        administration: {
          label: 'Administration',
          user_mgmt: 'Users',
          ingredient_mgmt: 'Ingredients',
          category_mgmt: 'Categories',
          gpio_mgmt: 'GPIO',
          pump_mgmt: 'Pumps',
          glass_mgmt: 'Glasses',
          event_mgmt: 'Events',
          system_mgmt: 'System'
        }
      },
      donate_btn_label: 'Donate'
    }
  },
  page: {
    login: {
      login_btn_label: 'Login',
      headline: 'Login',
      password_field_label: 'Password',
      username_field_label: 'Username',
      remember_me_label: 'Remember me',
      errors: {
        credentials_invalid: 'Username or password wrong!',
        server_unreachable: 'Couldn\'t contact server!'
      }
    },
    bar: {
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
      go_home_btn_label: 'Go Home',
      message: 'Oops. Nothing here...'
    },
    collections: {
      headline: 'My collections',
      add_btn_label: 'Create collection',
      refresh_btn_label: 'Refresh',
      no_collections_msg: 'No collections found!',
      create_dialog: {
        headline: 'Create collection',
        name_field_label: 'Name',
        abort_btn_label: 'Abort',
        create_btn_label: 'Create'
      }
    }
  },
  component: {}
}
