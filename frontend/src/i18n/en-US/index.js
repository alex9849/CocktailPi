export default {
  errors: {
    field_required: 'Required',
    not_valid_url: 'Not a valid URL',
    min_letters: 'Min {nr} letters',
    max_letters: 'Max {nr} letters',
    min_metric: 'Min {nr} {metric}',
    max_metric: 'Max {nr} {metric}',
    positive: 'Must be positive'
  },
  header: {
    machine_name: 'CocktailMaker',
    profile: {
      profile_btn_label: 'Profile',
      logout_btn_label: 'Logout'
    }
  },
  simple_header: {
    machine_name: 'CocktailMaker',
    leave_sv_btn_label: 'Leave simple-view',
    go_to_cocktail_progress_btn_label: 'Go to details >>',
    leave_sv_dialog: {
      headline: 'Leave simple view?',
      yes_btn_label: 'Yes',
      no_btn_label: 'No'
    }
  },
  simple_footer: {
    collections_btn_label: 'Collections',
    recipes_btn_label: 'Recipes',
    ingredients_btn_label: 'Ingredients'
  },
  common: {
    pump_fallback_name: 'Pump #{id}'
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
    },
    gpio_mgmt: {
      headline: 'GPIO Management',
      status_box: {
        headline: 'Status',
        pin_box: {
          headline: 'Pin usage',
          pin_usage: 'Pin usage:',
          gpio_boards: 'GPIO boards:'
        },
        i2c_box: {
          headline: 'I2C',
          configure_btn_label: 'Configure',
          status: 'Status',
          sda_pin: 'SDA-Pin:',
          scl_pin: 'SCL-Pin:'
        }
      },
      local_gpio_box: {
        headline: 'Local GPIOs:',
        no_boards_found: 'No boards found!'
      },
      i2c_expander_box: {
        add_btn_label: 'Add',
        headline: 'I2C GPIO Expanders:',
        i2c_disabled: 'I2C disabled!',
        no_expanders_found: 'No expanders found!'
      },
      delete_dialog: {
        headline: 'Delete {boardName}?',
        ok_btn_label: 'Delete',
        abort_btn_label: 'Abort'
      }
    },
    glass_mgmt: {
      headline: 'Glass Management',
      add_glass_btn_label: 'Create glass',
      refresh_btn_label: 'Refresh',
      glass_table: {
        columns: {
          name: 'Name',
          size: 'Size',
          default: 'Default',
          for_single_ingredients: 'For single ingredients',
          actions: 'Actions'
        },
        edit_btn_tooltip: 'Edit',
        delete_btn_tooltip: 'Delete',
        nr_glasses: '{nr} glasses in total'
      },
      notifications: {
        glass_updated: 'Glass updated successfully',
        glass_created: 'Glass created successfully'
      },
      edit_dialog: {
        headline_create: 'Create glass',
        headline_edit: 'Edit glass'
      },
      help_dialog: {
        headline: 'Glasses:',
        text: 'Different types of cocktails are typically served in various glass styles, each with its unique size. ' +
          'When placing a cocktail order, you have two options: you can either specify the desired liquid quantity ' +
          'for the machine to dispense or choose a specific glass type. If you opt for the latter, the machine will ' +
          'automatically dispense an amount of liquid that matches the selected glass\'s volume.\n\n' +
          'The liquid quantity that the machine should produce is always chosen by the ordering user. ' +
          'However, it is possible to assign each recipe a glass that is selected by default.\n\n' +
          'If no default glass has been selected for a particular recipe, the software will automatically default to ' +
          'using the predefined "default" glass.\n\n' +
          'It is possible to dispense single ingredients. The default glass for these orders can also be selected ' +
          'here using the "Use for single ingredients"-property.'
      }
    },
    event_mgmt: {
      headline: 'Event Management',
      add_btn_label: 'Add action',
      refresh_btn_label: 'Refresh',
      delete_btn_label: 'Delete selected actions',
      action_table: {
        columns: {
          trigger: 'Trigger',
          description: 'Description',
          comment: 'Comment',
          groups: 'Execution-groups',
          status: 'Status',
          actions: 'Actions'
        },
        view_logs_btn_tooltip: 'View logs',
        edit_btn_tooltip: 'Edit',
        delete_btn_tooltip: 'Delete'
      },
      delete_dialog: {
        headline: 'Delete Actions?'
      },
      edit_dialog: {
        headline_create: 'Create new action',
        headline_edit: 'Edit action'
      },
      notifications: {
        action_created: 'Action created successfully!',
        action_updated: 'Action updated successfully!'
      }
    },
    system_mgmt: {
      system: {
        headline: 'System Management',
        shutdown_btn_label: 'Shutdown system',
        shutdown_dialog: {
          headline: 'Shut down?',
          ok_btn_label: 'Shutdown',
          abort_btn_label: 'Abort'
        }
      },
      default_filter: {
        headline: 'Default filter',
        enable_btn_label: 'Enable default filter',
        fabricable: {
          headline: 'Fabricable:',
          show_all: 'Show all',
          fabricable_owned: 'Fabricable with owned ingredients',
          fabricable_auto: 'Fabricable fully automatic'
        },
        save_btn_label: 'Save',
        notifications: {
          settings_updated: 'Settings updated!'
        }
      }
    },
    simple_collections: {
      headline: 'Collections',
      no_collections_msg: 'No collections found!'
    },
    simple_recipes: {
      headline: 'Recipes',
      no_data_msg: 'No recipes found!'
    },
    simple_ingredient_recipes: {
      headline: 'Pumpable Ingredients',
      no_data_msg: 'No ingredients assigned to pumps!'
    },
    simple_collection: {
      headline: 'Collection: {name}',
      no_data_msg: 'No recipes found!'
    },
    simple_cocktail_progress: {
      headline: 'Current order',
      producing: 'Producing:',
      no_cocktail_in_progress: 'No cocktail is being prepared currently!',
      go_back_btn_label: '<< Go back',
      cancel_btn_label: 'Cancel order'
    },
    recipe_details: {
      edit_btn_label: 'Edit',
      produce_btn_label: 'Make cocktail',
      delete_btn_label: 'Delete',
      categories_headline: 'Categories:',
      none: 'None',
      default_glass_headline: 'Default glass:',
      description_headline: 'Description:',
      notifications: {
        recipe_deleted: 'Recipe deleted successfully'
      },
      delete_dialog: {
        headline: 'Are you sure, that you want to delete this recipe?',
        yes_btn_label: 'Delete',
        abort_btn_label: 'Abort'
      }
    },
    recipe_edit: {
      headline_new: 'Add Recipe',
      headline_edit: 'Edit Recipe',
      form: {
        name: 'Name',
        categories: 'Categories',
        image: 'Image',
        remove_image: 'Remove existing image',
        desc: 'Description',
        default_glass: 'Default glass'
      },
      save_btn_label: 'Create',
      update_btn_label: 'Update',
      abort_btn_label: 'Abort'
    },
    user_editor: {
      headline_edit: 'Edit user',
      headline_profile: 'My profile',
      headline_create: 'Create user',
      form: {
        columns: {
          username: 'Username',
          password: 'Password',
          role: 'Role',
          locked: 'Locked'
        },
        save_btn_label: 'Save',
        abort_btn_label: 'Abort',
        edit_btn_label: 'Edit',
        roles: {
          user: 'User',
          recipe_creator: 'Recipe-Creator',
          pump_ingredient_editor: 'Pump-Ingredient-Editor',
          admin: 'Admin'
        }
      },
      notifications: {
        user_created: 'User created successfully',
        user_updated: 'User updated successfully',
        profile_updated: 'Profile updated'
      }
    },
    i2c_mgmt: {
      headline: 'I2C configuration',
      form: {
        enable_label: 'Enable I2C',
        sda_pin_label: 'SDA Pin',
        scl_pin_label: 'SCL Pin',
        save_btn_label: 'Save',
        abort_btn_label: 'Abort'
      },
      tutorial: 'When enabling I2C two GPIO-pins on the local board get used up for the SDA and SCL pin of the I2C-bus. ' +
        'Some boards provide more then one I2C bus. The cocktailmaker software only supports one I2C bus a the time.\n' +
        'This supported bus is the device at ' +
        '<div class="q-badge flex inline items-center no-wrap q-badge--single-line q-badge--outline text-black" role="status">/sys/bus/i2c/devices/i2c-1</div> ' +
        'on the underlying linux filesystem.\n' +
        'On normal Raspberry PIs the pins used for SDL and SCL are on normally <b>2 for SDA</b> and <b>3 for SDL</b>\n' +
        '\n' +
        'If you can\'t see the pins that you want to use here, make sure that you don\'t have them assigned to something already.' +
        '<br><br>' +
        '<b>The fields for the SDL and SCL pin don\'t influence the selected bus.</b>',
      configuration_warning: '<b><u>WARNING!!!:</b></u> Enabling and disabling the I2C-bus will trigger unix commands that configure the I2C ' +
        'bus. Make sure that SDA and SCL are selected correctly. Otherwise it can happen, that pins are in use as normal pins and I2C pins at the same time. This will crash the application!'
    },
    pump_setup: {
      headline: 'Pump Setup Assistant',
      delete_btn_label: 'Delete Pump',
      name: {
        handle: 'Handle',
        headline: 'How should your pump be called?',
        pump_identifier_label: 'Pump identifier'
      },
      hw_pins: {
        handle: 'Hardware pins',
        headline: 'Select the pins that control the pump'
      },
      calibration: {
        handle: 'Calibrate',
        headline: 'Calibrate your pump',
        tube_capacity_label: 'Tube capacity (in ml)',
        tube_capacity_desc: 'The tube capacity determines how much liquid is needed to fill the hose that connects ' +
          'the liquid container with the dispensing part of your cocktail machine. This metric is used to ' +
          'accurately fill your hoses with liquid before actually producing a new drink. ' +
          'It is also used to empty your hoses (pump the liquid back into the container) if the machine has not ' +
          'been used for a while.',
        motor_tester: {
          headline: 'Motor tester',
          disable_reason_parameter_missing: 'Required pump-config parameter missing!',
          stepper_desc: 'Here you can test your motor and calculate the number of steps the motor needs to make to pump one cl. ' +
            'You can run the tester in two modes: ' +
            '<ul>' +
            '<li><b>Liquid:</b> Tell the motor how much liquid he should pump.</li>' +
            '<li><b>Steps:</b> Tell the motor how many steps he should take.</li>' +
            '</ul>' +
            'The tester is used to check and fine-tune your configuration.<br> ' +
            'You can also let the tester calculate the number of steps that the motor must make to pump one cl. ' +
            'For this, you have to measure the amount of liquid (in ml) that the pump pumped during your test. ' +
            'You can use a scale for that. Also make sure that your hoses are filled with liquid, as the tester ' +
            'does not take empty hoses or air bubbles into account! Afterwards a box will open, where you can enter your measurements. ' +
            'The tester will then correct the configuration according to your measurements.',
          dc_desc: 'Here you can test your motor and calculate the time it takes the motor to pump one cl.' +
            'You can run the tester in two modes:' +
            '<ul>\n' +
            '  <li><b>Liquid:</b> Tell the motor how much liquid he should pump.</li>' +
            '  <li><b>Time:</b> Tell the motor how many steps he should take.</li>' +
            '</ul>\n' +
            'The tester is used to check and fine-tune your configuration.<br>' +
            'You can also let the tester calculate the amount of time that the motor must run to pump one cl. ' +
            'For this, you have to measure the amount of liquid (in ml) that the pump pumped during your test. ' +
            'You can use a scale for that. Also make sure that your hoses are filled with liquid, as the tester ' +
            'does not take empty hoses or air bubbles into account! Afterwards a box will open, where you can enter your measurements. ' +
            'The tester will then correct the configuration according to your measurements!'
        }
      },
      state: {
        handle: 'State',
        headline: 'Pump state',
        pumped_up_label: 'Pumped up',
        pumped_up_desc: 'The "Pumped up"-field holds the information about the filling state of the hoses of a pump. ' +
          'If the hoses are not filled with liquid, the machine will fill them before producing a cocktail. ' +
          'This field is also used to find out from whose pumps the liquid should be pumped back into the ' +
          'container, if the machine is not used for a certain time.',
        filling_level_label: 'Current filling level',
        filling_level_desc: 'The current filling level of the container that is connected to the pump. ' +
          'This field is used to check if there is still enough liquid left ' +
          'to produce a cocktail of a certain size.',
        ingredient_label: 'Current ingredient',
        ingredient_desc: 'Optional: The ingredient that is currently connected to the pump.'
      },
      caption_complete: 'Complete',
      caption_optional: 'Optional',
      continue_step_btn_label: 'Continue',
      go_back_step_btn_label: 'Back',
      finish_setup_btn_label: 'Finish'
    }
  },
  component: {
    simple_recipes_filter_drawer: {
      headline: 'Filter',
      open_btn_label: 'Filter',
      name_field_label: 'Name',
      contains_ingredients_field_label: 'Contains ingredients',
      order_by_selector_label: 'Order by',
      fabricable_box: {
        headline: 'Fabricable:',
        show_all: 'Show all',
        show_all_desc: 'Shows all recipes',
        fabricable_owned: 'Fabricable with owned ingredients',
        fabricable_owned_desc: 'Only shows recipes that can be produced with owned ingredients',
        fabricable_auto: 'Fabricable fully automatic',
        fabricable_auto_desc: 'Only shows recipes that can be produced fully automatic'
      },
      search_btn_label: 'Search',
      reset_btn_label: 'Reset'
    },
    simple_collection_card: {
      name: '{name}',
      nr_cocktails: '{nr} cocktail(s)',
      desc: '{desc}',
      owner: 'by {owner}'
    },
    recipe_search_list: {
      loading: 'Loading...'
    },
    recipe_search_filter_card: {
      headline: 'Search-options',
      expert_search_label: 'Expert-search',
      cocktail_name_field_label: 'Search',
      fabricable_box: {
        headline: 'Fabricable:',
        show_all: 'Show all',
        fabricable_owned: 'Fabricable with owned ingredients',
        fabricable_auto: 'Fabricable fully automatic'
      },
      contains_ingredient_field_label: 'Contains ingredients',
      order_by_selector_label: 'Order by',
      reset_btn_label: 'Reset filters'
    },
    recipe_card: {
      owner_name: 'by {name}',
      ingredient_add_manually_tooltip: 'add manually',
      ingredient_not_owned_tooltip: 'not owned'
    },
    ingredient_list: {
      headline: 'Production-Steps',
      no_steps_added: 'No Production-Steps added!',
      alc_content: '{nr}% alcohol content',
      alc_content_range: '{min} - {min}% alcohol content',
      tag_boostable: 'Boostable',
      tag_unscaled: 'Unscaled',
      edit_dialog: {
        edit_headline: 'Edit Production-Step',
        add_headline: 'Add Production-Step',
        save_btn_label: 'Save',
        abort_btn_label: 'Abort'
      }
    },
    prod_step_editor: {
      tab_ingredient: 'Ingredient',
      tab_instruction: 'Instruction'
    },
    prod_step_editor_ingredient: {
      amount: 'Amount',
      amount_in: 'Amount (in {metric})',
      scale_label: 'Scale with volume',
      boostable_label: 'Boostable'
    },
    prod_step_editor_instruction: {
      instruction_label: 'Instruction'
    },
    ingredient_selector: {
      default_label: 'Ingredient',
      alc_content: '{nr}% alcohol content',
      alc_content_range: '{min} - {min}% alcohol content'
    },
    make_cocktail_dialog: {
      headline: 'Order Cocktail',
      order_btn_label: 'MAKE COCKTAIL ({nr} ml)'
    },
    make_cocktail_amount_to_produce: {
      glass_selector_label: 'Glass',
      amount_to_produce_label: 'Amount to produce'
    },
    make_cocktail_group_replacements: {
      fulfilled_msg: 'All ingredient-groups have been replaced with concrete ingredients!',
      not_fulfilled_msg: 'The following ingredient-groups have to get real existing ingredients assigned:',
      card: {
        prod_step_label: 'Production step',
        ingredient_group_label: 'Ingredient group',
        replacement_label: 'Replacement',
        tags: {
          in_bar: 'in bar',
          not_in_bar: 'not in bar',
          automatically_addable: 'Automatically addable'
        }
      }
    },
    make_cocktail_add_manually: {
      fulfilled_msg: 'All ingredients assigned to pumps! Cocktail can be produced fully automatic!',
      not_fulfilled_msg: 'The following ingredients have to get added manually or are not assigned to pumps. ' +
        'You will be asked to add them during the production progress:',
      tags: {
        in_bar: 'in bar',
        not_in_bar: 'not in bar'
      }
    },
    make_cocktail_occupied: {
      fulfilled_msg: 'Machine is not occupied!',
      occupied_cocktail_msg: 'Machine occupied! A cocktail ist getting prepared currently!',
      occupied_pumps_msg: 'Machine occupied! One or more pumps are getting cleaned/pumping up currently!'
    },
    make_cocktail_insufficient_ingredients: {
      fulfilled_msg: 'The following ingredients will be consumed:',
      not_fulfilled_msg: 'Can\'t make cocktail! Some pumps don\'t have enough liquid left:'
    },
    make_cocktail_customizer: {
      headline: 'Order customizer',
      headline_boosting: 'Boosting:',
      boosting_desc: 'Increases (or decreases) the ml of reported boostable ingredients in the base recipe. ' +
        '(Usually spirits) Non-boostable ingredients are decreased (or increased). The amount of liquid ' +
        'dispensed remains the same!',
      headline_additional_ingredients: 'Additional ingredients:',
      additional_ingredients_desc: 'Ingredients will be added as last production-step. The dispensed amount of ' +
        'liquid will be increased by the amount of ordered additional ingredients.',
      add_new_ingredient_btn_label: 'Add new ingredient',
      add_new_ingredient_headline: 'Add ingredient',
      recipe_not_boostable: 'Recipe not boostable!'
    },
    make_cocktail_pump_editor: {
      headline: 'Pump-Layout',
      pump_table: {
        required_ingredient: 'Required ingredient',
        columns: {
          nr: 'Nr',
          ingredient: 'Current Ingredient',
          filling_level: 'Remaining filling level',
          pumped_up: 'Pumped Up',
          state: 'State',
          actions: 'actions'
        },
        refill_btn_label: 'Fill',
        state_ok: 'OK',
        state_incomplete: 'Incomplete'
      }
    },
    pump_up_btn: {
      tooltip_up: 'Pump up',
      tooltip_back: 'Pump back'
    },
    pump_turn_on_btn: {
      tooltip_on: 'Turn on',
      tooltip_off: 'Turn off'
    },
    deleteWarning: {
      delete_btn_label: 'Delete',
      abort_btn_label: 'Abort',
      success_notification: 'Deleted successfully!',
      nothing_selected: 'Nothing selected!'
    },
    editDialog: {
      save_btn_label: 'Save',
      abort_btn_label: 'Abort'
    },
    ingredientGroupForm: {
      name: 'Name',
      parent_group: 'Parent group'
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
        delete_btn_tooltip: 'Delete',
        delete_dialog: {
          headline: 'Delete group?',
          warning: 'This also removes the selected group from all associated recipes!'
        }
      },
      edit_headline: 'Edit group',
      create_headline: 'Create group',
      notifications: {
        group_created: 'Group created successfully',
        group_updated: 'Group updated successfully'
      }
    },
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
      },
      edit_dialog: {
        headline_create: 'Create ingredient',
        headline_edit: 'Edit ingredient'
      },
      delete_dialog: {
        headline: 'Delete Ingredient?',
        warning: 'This also removes the selected group from all associated recipes!'
      },
      notifications: {
        ingredient_created: 'Ingredient created successfully',
        ingredient_updated: 'Ingredient updated successfully'
      }
    },
    ingredient_form: {
      name: 'Name',
      alc_content: 'Alcohol content',
      tab_automated: 'Automated',
      tab_manual: 'Manual',
      parent_group: 'Parent group',
      bottle_size: 'Bottle size',
      pump_time_multiplier: 'Pump time multiplier',
      unit: 'Unit',
      units: {
        gram: 'gram (g)',
        milliliter: 'milliliter (ml)',
        piece: 'piece(s)',
        teaspoon: 'teaspoon(s)',
        tablespoon: 'tablespoon(s)'
      }
    },
    gpio_expander_expansion_item: {
      caption_local: 'Board: Local, Usage: {pinsUsed}/{pinsMax}',
      caption_i2c: 'Address: {addr}, Board: {board}, Usage: {pinsUsed}/{pinsMax}'
    },
    pump_mgmt: {
      headline: 'Pump Management',
      add_btn_label: 'Add',
      start_all_btn_label: 'Start all',
      stop_all_btn_label: 'Stop all',
      no_pumps_found: 'No pumps found!',
      notifications: {
        all_stopped: 'All pumps stopped!',
        all_started: 'All pumps started!'
      }
    },
    pump_setup_type_selector: {
      headline: 'What type of pump do you want to add?',
      dc_pump: 'DC Pump',
      stepper_pump: 'Stepper Pump'
    },
    pump_card: {
      dc_pump: 'DC Pump',
      stepper_pump: 'Stepper Pump',
      option_missing: '-- missing --',
      no_ingredient_placeholder: 'None',
      fallack_name: 'Pump #{id}',
      attr: {
        ingredient: 'Ingredient',
        filling_level: 'filling level',
        time_per_cl: 'Time per Cl',
        enable_pin: 'Enable pin',
        running_state: 'Running state',
        tube_capacity: 'Tube capacity',
        steps_per_cl: 'Steps per Cl',
        acceleration: 'Acceleration',
        max_steps_per_second: 'Max steps per second',
        step_pin: 'Step pin'
      },
      pumpStates: {
        ready: 'Ready',
        incomplete: 'Incomplete',
        running: 'Running'
      },
      pumpUpStates: {
        pumped_up: 'Pumped Up',
        pumped_down: 'Pumped Down'
      },
      notifications: {
        pump_started: '{name} started!',
        pump_stopped: '{name} stopped!'
      }
    },
    reverse_pump_settings: {
      headline: 'Reverse pumping',
      form: {
        timer_options: {
          in_minutes: '{nr} Minutes',
          never: 'Never'
        },
        enable_label: 'Enable reverse pumping',
        vd_pin_headline: 'Voltage director pin',
        vd_pin_label: 'Director-Pin',
        overshoot_label: 'Overshoot',
        overshoot_hint: 'How strongly should number of ml be overshoot on pump back?',
        auto_pump_back_timer_label: 'Inactive time till automatic pump back',
        save_btn_label: 'Save'
      },
      notifications: {
        updated: 'Settings updated!'
      }
    }
  }
}
