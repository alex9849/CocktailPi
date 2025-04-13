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
    machine_name: 'CocktailPi',
    profile: {
      profile_btn_label: 'Profile',
      reload_btn_label: 'Reload',
      logout_btn_label: 'Logout'
    }
  },
  simple_header: {
    machine_name: 'CocktailPi',
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
            'CocktailPi now recognizes that \'Whisky 08/15\' can be utilized not only in recipes that ' +
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
          status: 'Status:',
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
          reboot_btn_label: 'Reboot',
          shutdown_btn_label: 'Shutdown',
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
      },
      appearance: {
        headline: 'Appearance'
      }
    },
    load_cell_mgmt: {
      headline: 'Load cell',
      hardware_settings: {
        headline: 'Load cell settings',
        enable_btn_label: 'Enable load cell',
        clk_pin_label: 'CLK-Pin',
        dt_pin_label: 'DT-Pin',
        save_btn_label: 'Save',
        save_and_return_btn_label: 'Save & Return'
      },
      calibration: {
        headline: 'Calibration',
        next_btn_label: 'Next',
        zero_point: {
          headline: 'Zero-Point Calibration (No Weight)',
          text: 'Ensure the dispensing area is empty. Press the Next button to record the load cell\'s zero-point measurement.'
        },
        known_weight: {
          headline: 'Known Weights Calibration',
          text: 'Place a known weight on the dispensing area. Enter the weight value in grams using the input field. Press the Next button to record the load cell\'s response to the known weight.',
          ref_weight_field_label: 'Reference weight (in g)'
        },
        validation: {
          headline: 'Validation Test',
          text: 'Place a random weight on the dispensing area. Press the Measure button to read the load cell\'s response to the known weight and compare it with the response.',
          measure_btn_label: 'Measure',
          measure_field_label: 'Measurement',
          finish_and_return_btn_label: 'Finish & Return',
          start_over_btn_label: 'Start over'
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
        'Some boards provide more then one I2C bus. The CocktailPi software only supports one I2C bus a the time.\n' +
        'This supported bus is the device at ' +
        '<div class="q-badge flex inline items-center no-wrap q-badge--single-line q-badge--outline text-black" role="status">/sys/bus/i2c/devices/i2c-1</div> ' +
        'on the underlying linux filesystem.\n' +
        'On normal Raspberry PIs the pins used for SDA and SCL are on normally <b>2 for SDA</b> and <b>3 for SCL</b>\n' +
        '\n' +
        'If you can\'t see the pins that you want to use here, make sure that you don\'t have them assigned to something already.' +
        '<br><br>' +
        '<b>The fields for the SDA and SCL pin don\'t influence the selected bus.</b>',
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
      finish_setup_btn_label: 'Finish',
      delete_dialog: {
        headline: 'Delete {name}?',
        yes_btn_label: 'Delete'
      },
      notifications: {
        pump_deleted: '{name} deleted!'
      }
    },
    collection: {
      modify_recipes: 'Modify recipes',
      stop_modify_recipes: 'Stop modifying recipes',
      delete: 'Delete collection',
      add_recipe: 'Add recipe',
      delete_dialog: {
        ok_btn_label: 'Delete',
        headline: 'Delete collection \'{name}\'?'
      },
      form: {
        name: 'Name',
        image: 'Image',
        remove_img: 'Remove existing image',
        desc: 'Description',
        edit_btn_label: 'Edit',
        save_btn_label: 'Save',
        abort_btn_label: 'Abort'
      }
    },
    notifications: {
      recipe_added: 'Recipe added successfully',
      recipe_removed: 'Recipe removed successfully',
      collection_updated: 'Collection updated successfully'
    }
  },
  component: {
    show_img_dialog: {
      close_btn_label: 'Close'
    },
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
    recipe_list: {
      no_recipes_found: 'No recipes found!'
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
      alc_content_range: '{min} - {max}% alcohol content',
      manual_instruction: 'Manual instruction:',
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
    pump_status: {
      headline: 'Status',
      configure_btn: 'Configure',
      pumps: {
        headline: 'Pumps',
        pumps_installed: 'Pumps installed:',
        ingredients_installed: 'Ingredients installed:'
      },
      reverse_pumping: {
        headline: 'Reverse pumping',
        status: 'Status:',
        status_enabled: 'Enabled',
        status_disabled: 'Disabled',
        overshoot: 'Overshoot:',
        timer: 'Timer:'
      },
      load_cell: {
        headline: 'Load cell',
        status: 'Status:',
        status_enabled: 'Enabled',
        status_disabled: 'Disabled',
        calibrated: 'Calibrated:',
        calibrated_yes: 'Yes',
        calibrated_no: 'No'
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
          has_image: 'Image',
          actions: 'Actions'
        },
        has_img_col: {
          no_label: 'No',
          yes_btn_label: 'Yes / Show'
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
      image: 'Image',
      remove_img: 'Remove existing image',
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
      valve: 'Valve',
      stepper_pump: 'Stepper Pump'
    },
    pump_card: {
      dc_pump: 'DC Pump',
      stepper_pump: 'Stepper Pump',
      valve: 'Valve',
      option_missing: '-- missing --',
      load_cell_not_calibrated: '-- not calibrated --',
      no_ingredient_placeholder: 'None',
      fallack_name: 'Pump #{id}',
      attr: {
        ingredient: 'Ingredient',
        filling_level: 'Filling level',
        time_per_cl: 'Time per Cl',
        enable_pin: 'Enable pin',
        running_state: 'Running state',
        tube_capacity: 'Tube capacity',
        steps_per_cl: 'Steps per Cl',
        acceleration: 'Acceleration',
        max_steps_per_second: 'Max steps per second',
        step_pin: 'Step pin',
        load_cell: 'Load cell'
      },
      pumpStates: {
        ready: 'Ready',
        incomplete: 'Incomplete',
        running: 'Running...'
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
        save_btn_label: 'Save & Return'
      },
      notifications: {
        updated: 'Settings updated!'
      }
    },
    pump_tester: {
      unknown_job_running: 'Unknown job running!',
      default_disable_reason: 'Disabled!',
      ref_metric: 'Metric:',
      metrics: {
        liquid: 'Liquid',
        liquid_pumped: 'Liquid pumped:',
        liquid_run_val_field: 'Ml to pump',
        steps: 'Steps',
        steps_made: 'Steps made:',
        steps_run_val_field: 'Steps to run',
        time: 'Time',
        time_taken: 'Time taken:',
        time_run_val_field: 'Ms to run',
        unknown_run_metric: 'Unknown metric'
      },
      true_liquid_pumped_field: 'Actual ml pumped',
      apply_per_cl_metric_value_btn_label: 'Apply',
      run_btn_label: 'Run',
      stop_btn_label: 'Stop'
    },
    pump_setup_dc_hw_pins: {
      control_pin_label: 'Control Pin',
      control_pin_desc: 'A DC motor can be switched on and off by connecting it to and disconnecting it from a power source. ' +
        'This is usually done with the aid of a relay. The relay opens and closes the electronic circuit to which the motor is connected. ' +
        'The "BCM-Pin" field contains the BCM number of the pin that controls the relay.<br><br>' +
        '<b>Important:</b> For the local board, that belongs to the Raspberry Pi Pin-numbers don\'t necessarily correspond ' +
        'to GPIO numbers, but BCM numbers. BCM refers to the “Broadcom SOC channel” number, which is the numbering inside ' +
        'the chip which is used on the Raspberry Pi. ' +
        'These numbers changed between board versions. This link may help:',
      power_state_desc: 'Depending on our setup the motor might run either if the GPIO-pin that controls the pump is set to high or low. Please select the pin-state in which your pump would run in your configuration. ',
      power_state_label: 'Power state',
      power_state_options: {
        high: 'High',
        low: 'Low'
      }
    },
    pump_setup_dc_calibration: {
      time_per_cl_pin_label: 'Time per cl in ms',
      time_per_cl_pin_desc: '"Time per cl in ms" determines how many milliseconds (ms) the pump must run to pump one centiliter (cl). ' +
        'This value is used to determine how long the pump must run to pump the desired amount of liquid from the bottle.'
    },
    pump_setup_stepper_hw_pins: {
      step_pin_label: '(local) Step Pin',
      enable_pin_label: 'Enable Pin',
      pin_desc: 'A stepper motor driver usually has three important pins, that are used to control the motor.' +
        '<ul>' +
        '        <li>' +
        '          The step pin, which receives one pulse for each step the motor is to make.' +
        '        </li>' +
        '        <li>' +
        '          The enable pin. This pin decides whether the motor should be energized and thus actively ' +
        '          hold its current position or not.' +
        '        </li>' +
        '        <li>' +
        '          The direction pin. It decides on the direction that the motor takes. The direction that' +
        '          the motor is running to is decided by one single pin, that controls the direction for all motors. ' +
        '          That pin gets defined globally and is not part of this setup! ' +
        '          Please build your machine in a way that connects that pin with the direction logic of all ' +
        '          your motors.' +
        '        </li>' +
        '        <li>' +
        '          Your motor driver very likely also provides more pins (step resolution/sleep/...). Please ' +
        '          configure these statically in hardware!' +
        '        </li>' +
        '      </ul>' +
        '<b>Important:</b> For the local board, that belongs to the Raspberry Pi Pin-numbers don\'t necessarily correspond ' +
        '        to GPIO numbers, but BCM numbers. BCM refers to the “Broadcom SOC channel” number, which is the numbering inside ' +
        '        the chip which is used on the Raspberry Pi. ' +
        '        These numbers changed between board versions. This link may help:'
    },
    pump_setup_stepper_calibration: {
      acceleration_label: 'Acceleration',
      acceleration_desc: 'The acceleration field determines how fast the motor should accelerate or decelerate. ' +
        'If the acceleration is too high, the motor may skip steps when accelerating or take too many steps when decelerating. ' +
        'The acceleration is given in steps per second per second.',
      max_steps_per_second_label: 'Max steps per second',
      max_steps_per_second_desc: '<p>' +
        '        The "max steps per second"-field determines fast the motor should spin at max. ' +
        '        One revolution is normally divided into 200 steps. This can vary depending on the motor and motor ' +
        '        driver settings.' +
        '        If the value is too high, the motor may not be able to keep up and may skip steps or even not run at all. ' +
        '        If the value is too low, the motor will run slower than necessary.<br>' +
        '      </p>' +
        '      <p>' +
        '        The rule is:' +
        '      </p>' +
        '      <ul>' +
        '        <li>higher = faster motor</li>' +
        '        <li>lower = slower motor</li>' +
        '      </ul>',
      steps_per_cl_label: 'Steps per cl',
      steps_per_cl_desc: 'This field determines how many steps the motor must take to pump a cl.'
    },
    glass_form: {
      name: 'Name',
      size: 'Size',
      default_checkbox: 'Default',
      use_for_single_ingredients_checkbox: 'Use for single ingredients'
    },
    event_action_editor_form: {
      comment_label: 'Comment',
      execution_groups: {
        label: 'Execution-groups',
        write_to_create: 'Write to create new group...',
        create_new: '<b>Create new:</b> {group}'
      },
      trigger_label: 'Trigger',
      action: {
        label: 'Action',
        select_action: 'Select action to view options...',
        options: {
          call_url: 'Call URL',
          audio: 'Play audio file',
          python: 'Execute python script',
          nothing: 'Do nothing'
        },
        call_url: {
          request_method_label: 'Request method',
          url_label: 'URL'
        },
        audio: {
          file_label: 'Audio (max. 20 MB)',
          volume: 'Volume {nr}%',
          output_device_label: 'Output device',
          on_repeat_label: 'On repeat'
        },
        python: {
          desc: 'Executes a python file using python 3. ' +
            'You can also view the console output of your program in real-time.',
          install_lib_tip: 'New libraries can be installed using <b>&#60;root folder&#62;/venv/bin/pip3 install &#60;library&#62;</b>, where "root folder" corresponds to the folder containing the cocktailpi jar file. Please note that the venv folder is only created after the Python libraries have been scanned using the button below or a Python script has been executed via the event system.',
          fetching_libs: 'Fetching Libraries',
          lib_list_lib: 'Library',
          lib_list_version: 'Version',
          no_libs_found: 'No libraries installed',
          python_file_label: 'Python (max. 20 MB)'
        },
        nothing: {
          desc: 'This action does nothing. But it will cancel other running actions that don\'t share execution-groups with it.'
        }
      }
    },
    circular_cocktail_progress: {
      headline: 'Currently fabricated cocktail',
      no_cocktail_msg: 'Currently, no cocktail gets fabricated!<br>' +
        'Go to "My recipes" or "Public recipes" to put one in order.',
      close_btn_label: 'Close'
    },
    cocktail_production_manual_step_card: {
      continue_btn_label: 'Continue',
      add_ingredient_headline: 'Please manually add the following ingredients and click "continue":'
    },
    donation_disclaimer: {
      headline: 'Your support is needed!',
      headline_caption: 'Scroll down to close',
      donate_paypal_btn_label: 'Donate via PayPal',
      donate_github_btn_label: 'Donate via GitHub sponsors',
      headline_2: 'This software is free, but donations are appreciated!',
      introduction: 'This software is free, but still it took and takes a lot time to develop and maintain it. ' +
        'The developer of CocktailPi (me) develops it in his free time. I\'m also a student who doesn\'t have a ' +
        'real income yet.',
      main_text: '<p>Here are some more reasons why you should donate:</p>' +
        '            <ul>\n' +
        '              <li>\n' +
        '                Users of this software usually spent large amounts of money on hardware (a Raspberry Pi, Pumps, a case,\n' +
        '                ...).\n' +
        '                Nevertheless this hardware wouldn\'t function if the CocktailPi software wouldn\'t exist.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                You might think that that someone else will donate, but sadly they think that too. Before adding this\n' +
        '                disclaimer I got around 50€ of donations in two years\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                I usually don\'t get any feedback for the software. It doesn\'t collect any data. I have no idea how many\n' +
        '                people are out there using it.\n' +
        '                A donation and also "stars" on GitHub give me positive feedback and motivate me to continue working on\n' +
        '                CocktailPi.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                Developing this software causes costs. I as a developer often buy hardware, just to test\n' +
        '                if it would function with the device and make sense.\n' +
        '              </li>\n' +
        '            </ul>' +
        '            <p>\n' +
        '              Donating is entirely voluntary. While the software remains free to use, your contribution\n' +
        '              demonstrates your appreciation for the hard work and dedication, that were required to develop this software.\n' +
        '              A donation also motivates me to continue refining and expanding the software\'s capabilities.\n' +
        '            </p>\n' +
        '            <p>\n' +
        '              You can donate using GitHub Sponsors or Paypal. You can pick any amount that you think that the software\n' +
        '              is worth to you.\n' +
        '              You can also do monthly donations if you want to support me and my work over a period of time.\n' +
        '            </p>',
      action_box: {
        donated: {
          headline: 'Thank you for your donation. You made a difference!',
          caption: 'This happens less often then you might think. Thank you very much!',
          close_btn: 'Great people button (Close disclaimer)',
          revert_btn: 'I didn\'t donate'
        },
        not_donated: {
          headline: 'Thank you very much!',
          donated_btn: 'I made a donation',
          close_btn: 'No, remind me later'
        },
        lying_is_no_nice: {
          headline: 'Please note that lying is not nice.',
          confirm_btn: 'Confirm',
          checkbox: 'I\'m not a liar',
          go_back: 'Go back'
        }
      }
    },
    settings_appearance: {
      language: 'Language',
      save_btn_label: 'Save',
      notifications: {
        settings_updated: 'Settings updated!'
      },
      colors: {
        interface: {
          headline: 'Interface colors',
          header: 'Header',
          sidebar: 'Sidebar',
          background: 'Background',
          btn_primary: 'Button / Primary',
          btn_navigation_active: 'Button / Navigation active',
          card_header: 'Card / Header',
          card_body: 'Card / Body',
          card_item_group: 'Card / Item group'
        },
        simple_view: {
          headline: 'Simple view colors',
          header: 'Header + Footer',
          sidebar: 'Sidebar',
          background: 'Background',
          btn_primary: 'Button / Primary',
          btn_navigation: 'Button / Navigation',
          btn_navigation_active: 'Button / Navigation active',
          cocktail_progress: 'Cocktail progress banner',
          card: 'Card'
        }
      }
    }
  },
  constants: {
    recipe_order_options: {
      name_asc: 'Name asc',
      name_desc: 'Name desc',
      last_update_asc: 'Last update',
      last_update_desc: 'Least update'
    },
    event_action_trigger_display_names: {
      cocktail_prod_started: 'Cocktail production started',
      cocktail_prod_manual_interaction_requested: 'Cocktail production manual interaction requested',
      cocktail_prod_manual_interaction_completed: 'Cocktail production manual interaction completed',
      cocktail_prod_finished: 'Cocktail production finished',
      cocktail_prod_canceled: 'Cocktail production canceled',
      application_started: 'Application started'
    }
  }
}
