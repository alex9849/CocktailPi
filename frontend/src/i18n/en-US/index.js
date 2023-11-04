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
    },
    recipes: {
      my_recipes_headline: 'My recipes',
      all_recipes_headline: 'Public recipes',
      create_recipe_btn_label: 'Create recipe',
      refresh_btn_label: 'Refresh'
    },
    ingredient_recipes: {
      headline: 'Pumpable ingredients',
      no_ingredients_available: 'No ingredients assigned to pumps!',
      recipe: {
        ml_left: '{nr} ml left',
        alc_content: '{nr}% alcohol'
      }
    },
    user_mgmt: {
      headline: 'User Management',
      create_user_btn_label: 'Create user',
      refresh_users_btn_label: 'Refresh',
      edit_user_btn_tooltip: 'Edit',
      delete_user_btn_tooltip: 'Delete',
      user_table: {
        nr_users: '{nr} user(s) in total',
        columns: {
          username: 'Username',
          active: 'Active',
          role: 'Role',
          actions: 'Actions'
        }
      },
      delete_dialog: {
        headline: 'The following user will be deleted:'
      }
    },
    ingredient_mgmt: {
      headline: 'Ingredient Management',
      help_dialog: {
        ingredients: {
          headline: 'Ingredients:',
          text: 'Ingredients are concrete ingredients that can be directly added to a glass and can be bought from the ' +
            'supermarket. Ingredients can usually be assigned to a pump. '
        },
        groups: {
          headline: 'Ingredient groups:',
          text: ' Ingredient groups are groups that consist of multiple "child ingredients" or other ingredient groups. ' +
            'Recipes can contain ingredient groups. Before ordering such an recipe, the user has to replace that ' +
            'ingredient group with an ingredient that belongs to that group. The software automatically does that ' +
            'for the user, if such an ingredient was assigned to a pump or is marked as being in the \'Bar\'. ' +
            'Ingredients on pumps get preferred.\n' +
            '\n' +
            'Example: You have a specific ingredient called \'Whisky 08/15\'. This whisky is categorized under the ' +
            'ingredient group \'Bourbon\'. The group \'Bourbon\', in turn, falls under the broader category ' +
            '\'Whisky\'. There is an additional child ingredient group related to \'Whisky\' known as \'Scotch\'.\n' +
            '\n' +
            'You\'ve included \'Whisky 08/15\' in your bar\'s inventory. Due to the hierarchical structure, ' +
            'CocktailMaker now recognizes that \'Whisky 08/15\' can be utilized not only in recipes that ' +
            'specifically require this ingredient, but also in various recipes that call for any type of \'Bourbon\' ' +
            'or \'Whisky\'. However, it\'s important to note that \'Whisky 08/15\' cannot substitute for recipes that ' +
            'specifically demand \'Scotch\' or other distinct ingredients. '
        },
        close_btn_label: 'Close'
      },
      ingredient_groups: {
        headline: 'Ingredient groups',
        create_btn_label: 'Create group',
        refresh_btn_label: 'Refresh',
        group_table: {
          search_field_label: 'Search',
          columns: {
            group: 'Group',
            alc_content: 'Alcohol content',
            parent_group: 'Parent group',
            actions: 'Actions'
          },
          edit_btn_tooltip: 'Edit',
          delete_btn_tooltip: 'Delete'
        }
      },
      ingredients: {
        headline: 'Ingredients',
        create_btn_label: 'Create ingredient',
        refresh_btn_label: 'Refresh',
        ingredient_table: {
          search_field_label: 'Search',
          columns: {
            ingredient: 'Ingredient',
            type: 'Type',
            alc_content: 'Alcohol content',
            bottle_size: 'Bottle size',
            unit: 'Unit',
            pump_time_multiplier: 'Pump time multiplier',
            parent_group: 'Parent Group',
            actions: 'Actions'
          },
          edit_btn_tooltip: 'Edit',
          delete_btn_tooltip: 'Delete'
        }
      }
    },
    category_mgmt: {
      headline: 'Category Management',
      create_btn_label: 'Create category',
      refresh_btn_label: 'Refresh',
      no_data_msg: 'No categories found!',
      delete_dialog: {
        headline: 'The following categorie(s) will be deleted:',
        ok_btn_label: 'Delete',
        abort_btn_label: 'Abort'
      },
      category_table: {
        columns: {
          category: 'Category',
          actions: 'Actions'
        },
        edit_btn_tooltip: 'Edit',
        delete_btn_tooltip: 'Delete',
        nr_categories: '{nr} categorie(s) in total'
      },
      notifications: {
        category_updated: 'Category updated successfully',
        category_created: 'Category created successfully',
        category_deleted: 'Category deleted successfully'
      },
      create_dialog: {
        headline_edit: 'Edit category',
        headline_create: 'Create category',
        name_field_label: 'Name',
        save_btn_label: 'Save',
        abort_btn_label: 'Abort'
      }
    }
  },
  component: {
    // Not added yet
    ingredient_mgmt: {
      create_btn_label: 'Create ingredient',
      refresh_btn_label: 'Refresh',
      ingredient_table: {
        search_field_label: 'Search',
        columns: {
          ingredient: 'Ingredient',
          type: 'Type',
          alc_content: 'Alcohol content',
          bottle_size: 'Bottle size',
          unit: 'Unit',
          pump_time_multiplier: 'Pump time multiplier',
          parent_group: 'Parent Group',
          actions: 'Actions'
        },
        edit_btn_tooltip: 'Edit',
        delete_btn_tooltip: 'Delete'
      }
    },
    ingredient_group_mgmt: {
      create_btn_label: 'Create group',
      refresh_btn_label: 'Refresh',
      group_table: {
        search_field_label: 'Search',
        columns: {
          group: 'Group',
          alc_content: 'Alcohol content',
          parent_group: 'Parent group',
          actions: 'Actions'
        },
        edit_btn_tooltip: 'Edit',
        delete_btn_tooltip: 'Delete'
      }
    }
  }
}
