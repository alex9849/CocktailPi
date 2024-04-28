export default {
  errors: {
    field_required: 'Påkrævet',
    not_valid_url: 'Ikke en gyldig URL',
    min_letters: 'Min {nr} bogstaver',
    max_letters: 'Max {nr} bogstaver',
    min_metric: 'Min {nr} {metric}',
    max_metric: 'Max {nr} {metric}',
    positive: 'Skal være positiv'
  },
  header: {
    machine_name: 'CocktailPi',
    profile: {
      profile_btn_label: 'Profil',
      reload_btn_label: 'Genindlæs',
      logout_btn_label: 'Log ud'
    }
  },
  simple_header: {
    machine_name: 'CocktailPi',
    leave_sv_btn_label: 'Forlad simpel-visning',
    go_to_cocktail_progress_btn_label: 'Gå til detaljer >>',
    leave_sv_dialog: {
      headline: 'Forlade simpel visning?',
      yes_btn_label: 'Ja',
      no_btn_label: 'Nej'
    }
  },
  simple_footer: {
    collections_btn_label: 'Samlinger',
    recipes_btn_label: 'Opskrifter',
    ingredients_btn_label: 'Ingredienser'
  },
  common: {
    pump_fallback_name: 'Pumpe #{id}'
  },
  layout: {
    full_layout: {
      sidebar: {
        me: {
          label: 'Mig',
          dashboard: 'Dashboard',
          simple_view: 'Simpel visning',
          bar: 'Bar',
          collections: 'Samlinger',
          my_recipes: 'Mine opskrifter'
        },
        cocktails: {
          label: 'Cocktails',
          category_all: 'Alle',
          ingredients: 'Ingredienser'
        },
        administration: {
          label: 'Administration',
          user_mgmt: 'Brugere',
          ingredient_mgmt: 'Ingredienser',
          category_mgmt: 'Kategorier',
          gpio_mgmt: 'GPIO',
          pump_mgmt: 'Pumper',
          glass_mgmt: 'Glas',
          event_mgmt: 'Begivenheder',
          system_mgmt: 'System'
        }
      },
      donate_btn_label: 'Donér'
    }
  },
  page: {
    login: {
      login_btn_label: 'Log ind',
      headline: 'Log ind',
      password_field_label: 'Adgangskode',
      username_field_label: 'Brugernavn',
      remember_me_label: 'Husk mig',
      errors: {
        credentials_invalid: 'Brugernavn eller adgangskode forkert!',
        server_unreachable: 'Kunne ikke kontakte serveren!'
      }
    },
    bar: {
      headline: 'Ingredienser ejet',
      add_btn_label: 'Tilføj ingrediens',
      refresh_btn_label: 'Opdater',
      owned_table: {
        nr_ingredients_owned: '{nrIngredients} ingrediens(er) i alt',
        no_data_msg: 'Ingen ingredienser tilføjet',
        columns: {
          ingredient: 'Ingrediens',
          alc_content: 'Alkoholindhold',
          actions: 'Handlinger'
        },
        delete_btn_tooltip: 'Slet'
      },
      add_dialog: {
        headline: 'Tilføj Ingrediens',
        ingredient_selector_label: 'Ingrediens',
        abort_btn_label: 'Afbryd',
        save_btn_label: 'Gem',
        required_error: 'Påkrævet'
      },
      notifications: {
        ingredient_added: 'Ingrediens tilføjet succesfuldt',
        ingredient_removed: 'Ingrediens fjernet succesfuldt'
      }
    },
    404: {
      go_home_btn_label: 'Gå hjem',
      message: 'Ups. Intet her...'
    },
    collections: {
      headline: 'Mine samlinger',
      add_btn_label: 'Opret samling',
      refresh_btn_label: 'Opdater',
      no_collections_msg: 'Ingen samlinger fundet!',
      create_dialog: {
        headline: 'Opret samling',
        name_field_label: 'Navn',
        abort_btn_label: 'Afbryd',
        create_btn_label: 'Opret'
      }
    },
    recipes: {
      my_recipes_headline: 'Mine opskrifter',
      all_recipes_headline: 'Offentlige opskrifter',
      create_recipe_btn_label: 'Opret opskrift',
      refresh_btn_label: 'Opdater'
    },
    ingredient_recipes: {
      headline: 'Pumpbare ingredienser',
      no_ingredients_available: 'Ingen ingredienser tildelt pumper!',
      recipe: {
        ml_left: '{nr} ml tilbage',
        alc_content: '{nr}% alkohol'
      }
    },
    user_mgmt: {
      headline: 'Brugerstyring',
      create_user_btn_label: 'Opret bruger',
      refresh_users_btn_label: 'Opdater',
      edit_user_btn_tooltip: 'Rediger',
      delete_user_btn_tooltip: 'Slet',
      user_table: {
        nr_users: '{nr} bruger(e) i alt',
        columns: {
          username: 'Brugernavn',
          active: 'Aktiv',
          role: 'Rolle',
          actions: 'Handlinger'
        }
      },
      delete_dialog: {
        headline: 'Følgende bruger vil blive slettet:'
      }
    },
    ingredient_mgmt: {
      headline: 'Ingrediensstyring',
      help_dialog: {
        ingredients: {
          headline: 'Ingredienser:',
          text: 'Ingredienser er konkrete ingredienser, der kan tilføjes direkte til et glas og kan købes i supermarkedet. Ingredienser kan normalt tildeles en pumpe.'
        },
        groups: {
          headline: 'Ingrediensgrupper:',
          text: 'Ingrediensgrupper er grupper, der består af flere "barn ingredienser" eller andre ingrediensgrupper. Opskrifter kan indeholde ingrediensgrupper. Før bestilling af sådan en opskrift, skal brugeren erstatte den ingrediensgruppe med en ingrediens, der tilhører den gruppe. Softwaren gør automatisk dette for brugeren, hvis en sådan ingrediens er tildelt en pumpe eller er markeret som værende i \'Baren\'. Ingredienser på pumper foretrækkes. \n\nEksempel: Du har en specifik ingrediens kaldet \'Whisky 08/15\'. Denne whisky er kategoriseret under ingrediensgruppen \'Bourbon\', som igen falder under den bredere kategori \'Whisky\'. Der er en yderligere barn ingrediensgruppe relateret til \'Whisky\' kendt som \'Scotch\'.\n\nDu har inkluderet \'Whisky 08/15\' i din bars lager. På grund af den hierarkiske struktur genkender CocktailPi nu, at \'Whisky 08/15\' kan anvendes ikke kun i opskrifter, der specifikt kræver denne ingrediens, men også i forskellige opskrifter, der kræver en hvilken som helst type \'Bourbon\' eller \'Whisky\'. Det er dog vigtigt at bemærke, at \'Whisky 08/15\' ikke kan erstatte opskrifter, der specifikt kræver \'Scotch\' eller andre forskellige ingredienser.'
        },
        close_btn_label: 'Luk'
      },
      ingredient_groups: {
        headline: 'Ingrediensgrupper',
        create_btn_label: 'Opret gruppe',
        refresh_btn_label: 'Opdater',
        group_table: {
          search_field_label: 'Søg',
          columns: {
            group: 'Gruppe',
            alc_content: 'Alkoholindhold',
            parent_group: 'Forældergruppe',
            actions: 'Handlinger'
          },
          edit_btn_tooltip: 'Rediger',
          delete_btn_tooltip: 'Slet'
        }
      },
      ingredients: {
        headline: 'Ingredienser',
        create_btn_label: 'Opret ingrediens',
        refresh_btn_label: 'Opdater',
        ingredient_table: {
          search_field_label: 'Søg',
          columns: {
            ingredient: 'Ingrediens',
            type: 'Type',
            alc_content: 'Alkoholindhold',
            bottle_size: 'Flaskestørrelse',
            unit: 'Enhed',
            pump_time_multiplier: 'Pumpe tidsmultiplikator',
            parent_group: 'Forældergruppe',
            actions: 'Handlinger'
          },
          edit_btn_tooltip: 'Rediger',
          delete_btn_tooltip: 'Slet'
        }
      }
    },
    category_mgmt: {
      headline: 'Kategoristyring',
      create_btn_label: 'Opret kategori',
      refresh_btn_label: 'Opdater',
      no_data_msg: 'Ingen kategorier fundet!',
      delete_dialog: {
        headline: 'Følgende kategori(er) vil blive slettet:',
        ok_btn_label: 'Slet',
        abort_btn_label: 'Afbryd'
      },
      category_table: {
        columns: {
          category: 'Kategori',
          actions: 'Handlinger'
        },
        edit_btn_tooltip: 'Rediger',
        delete_btn_tooltip: 'Slet',
        nr_categories: '{nr} kategori(er) i alt'
      },
      notifications: {
        category_updated: 'Kategori opdateret succesfuldt',
        category_created: 'Kategori oprettet succesfuldt',
        category_deleted: 'Kategori slettet succesfuldt'
      },
      create_dialog: {
        headline_edit: 'Rediger kategori',
        headline_create: 'Opret kategori',
        name_field_label: 'Navn',
        save_btn_label: 'Gem',
        abort_btn_label: 'Afbryd'
      }
    },
    gpio_mgmt: {
      headline: 'GPIO-styring',
      status_box: {
        headline: 'Status',
        pin_box: {
          headline: 'Pin brug',
          pin_usage: 'Pin brug:',
          gpio_boards: 'GPIO boards:'
        }
      },
      local_gpio_box: {
        headline: 'Lokale GPIOs:',
        no_boards_found: 'Ingen boards fundet!'
      },
      i2c_expander_box: {
        add_btn_label: 'Tilføj',
        headline: 'I2C GPIO Expandere:',
        i2c_disabled: 'I2C deaktiveret!',
        no_expanders_found: 'Ingen expandere fundet!'
      },
      delete_dialog: {
        headline: 'Slet {boardName}?',
        ok_btn_label: 'Slet',
        abort_btn_label: 'Afbryd'
      }
    },
    glass_mgmt: {
      headline: 'Glasstyring',
      add_glass_btn_label: 'Opret glas',
      refresh_btn_label: 'Opdater',
      glass_table: {
        columns: {
          name: 'Navn',
          size: 'Størrelse',
          default: 'Standard',
          for_single_ingredients: 'Til enkelte ingredienser',
          actions: 'Handlinger'
        },
        edit_btn_tooltip: 'Rediger',
        delete_btn_tooltip: 'Slet',
        nr_glasses: '{nr} glas i alt'
      },
      notifications: {
        glass_updated: 'Glas opdateret succesfuldt',
        glass_created: 'Glas oprettet succesfuldt'
      },
      edit_dialog: {
        headline_create: 'Opret glas',
        headline_edit: 'Rediger glas'
      },
      help_dialog: {
        headline: 'Glas:',
        text: "Forskellige typer cocktails serveres typisk i forskellige glasstile, hver med sin unikke størrelse. Når du bestiller en cocktail, har du to muligheder: du kan enten specificere den ønskede væskemængde for maskinen at dispensere eller vælge en specifik glastype. Hvis du vælger sidstnævnte, vil maskinen automatisk dispensere en mængde væske, der passer til det valgte glas volumen.\n\nDen væskemængde, som maskinen skal producere, vælges altid af den bestillende bruger. Det er dog muligt at tildele hvert opskrift et glas, der vælges som standard.\n\nHvis der ikke er valgt et standardglas for en bestemt opskrift, vil softwaren automatisk standardisere til at bruge det foruddefinerede 'standard' glas.\n\nDet er muligt at dispensere enkelte ingredienser. Standardglasset for disse ordrer kan også vælges her ved hjælp af egenskaben 'Brug til enkelte ingredienser'."
      }
    },
    event_mgmt: {
      headline: 'Begivenhedsstyring',
      add_btn_label: 'Tilføj handling',
      refresh_btn_label: 'Opdater',
      delete_btn_label: 'Slet valgte handlinger',
      action_table: {
        columns: {
          trigger: 'Udløser',
          description: 'Beskrivelse',
          comment: 'Kommentar',
          groups: 'Udførelsesgrupper',
          status: 'Status',
          actions: 'Handlinger'
        },
        view_logs_btn_tooltip: 'Vis logs',
        edit_btn_tooltip: 'Rediger',
        delete_btn_tooltip: 'Slet'
      },
      delete_dialog: {
        headline: 'Slet Handlinger?'
      },
      edit_dialog: {
        headline_create: 'Opret ny handling',
        headline_edit: 'Rediger handling'
      },
      notifications: {
        action_created: 'Handling oprettet succesfuldt!',
        action_updated: 'Handling opdateret succesfuldt!'
      }
    },
    system_mgmt: {
      system: {
        headline: 'Systemstyring',
        shutdown_btn_label: 'Luk systemet',
        shutdown_dialog: {
          headline: 'Luk ned?',
          reboot_btn_label: 'Genstart',
          shutdown_btn_label: 'Luk ned',
          abort_btn_label: 'Afbryd'
        }
      },
      default_filter: {
        headline: 'Standardfilter',
        enable_btn_label: 'Aktivér standardfilter',
        fabricable: {
          headline: 'Fremstillelig:',
          show_all: 'Vis alle',
          fabricable_owned: 'Fremstillelig med ejede ingredienser',
          fabricable_auto: 'Fremstillelig fuldautomatisk'
        },
        save_btn_label: 'Gem',
        notifications: {
          settings_updated: 'Indstillinger opdateret!'
        }
      },
      appearance: {
        headline: 'Udseende'
      }
    },
    simple_collections: {
      headline: 'Samlinger',
      no_collections_msg: 'Ingen samlinger fundet!'
    },
    simple_recipes: {
      headline: 'Opskrifter',
      no_data_msg: 'Ingen opskrifter fundet!'
    },
    simple_ingredient_recipes: {
      headline: 'Pumpbare Ingredienser',
      no_data_msg: 'Ingen ingredienser tildelt pumper!'
    },
    simple_collection: {
      headline: 'Samling: {name}',
      no_data_msg: 'Ingen opskrifter fundet!'
    },
    simple_cocktail_progress: {
      headline: 'Nuværende ordre',
      producing: 'Producerer:',
      no_cocktail_in_progress: 'Der forberedes ingen cocktail i øjeblikket!',
      go_back_btn_label: '<< Gå tilbage',
      cancel_btn_label: 'Annuller ordre'
    },
    recipe_details: {
      edit_btn_label: 'Rediger',
      produce_btn_label: 'Lav cocktail',
      delete_btn_label: 'Slet',
      categories_headline: 'Kategorier:',
      none: 'Ingen',
      default_glass_headline: 'Standardglas:',
      description_headline: 'Beskrivelse:',
      notifications: {
        recipe_deleted: 'Opskrift slettet succesfuldt'
      },
      delete_dialog: {
        headline: 'Er du sikker på, at du vil slette denne opskrift?',
        yes_btn_label: 'Slet',
        abort_btn_label: 'Afbryd'
      }
    },
    recipe_edit: {
      headline_new: 'Tilføj Opskrift',
      headline_edit: 'Rediger Opskrift',
      form: {
        name: 'Navn',
        categories: 'Kategorier',
        image: 'Billede',
        remove_image: 'Fjern eksisterende billede',
        desc: 'Beskrivelse',
        default_glass: 'Standardglas'
      },
      save_btn_label: 'Opret',
      update_btn_label: 'Opdater',
      abort_btn_label: 'Afbryd'
    },
    user_editor: {
      headline_edit: 'Rediger bruger',
      headline_profile: 'Min profil',
      headline_create: 'Opret bruger',
      form: {
        columns: {
          username: 'Brugernavn',
          password: 'Adgangskode',
          role: 'Rolle',
          locked: 'Låst'
        },
        save_btn_label: 'Gem',
        abort_btn_label: 'Afbryd',
        edit_btn_label: 'Rediger',
        roles: {
          user: 'Bruger',
          recipe_creator: 'Opskrifts-Opretter',
          pump_ingredient_editor: 'Pumpe-Ingrediens-Redaktør',
          admin: 'Admin'
        }
      },
      notifications: {
        user_created: 'Bruger oprettet succesfuldt',
        user_updated: 'Bruger opdateret succesfuldt',
        profile_updated: 'Profil opdateret'
      }
    },
    i2c_mgmt: {
      headline: 'I2C-konfiguration',
      form: {
        enable_label: 'Aktivér I2C',
        sda_pin_label: 'SDA Pin',
        scl_pin_label: 'SCL Pin',
        save_btn_label: 'Gem',
        abort_btn_label: 'Afbryd'
      },
      tutorial: "Når I2C aktiveres, bruges to GPIO-pins på det lokale board til SDA- og SCL-pinnen på I2C-bussen. Nogle boards tilbyder mere end en I2C-bus. CocktailPi-softwaren understøtter kun en I2C-bus ad gangen.\nDette understøttede bus er enheden på '/sys/bus/i2c/devices/i2c-1' på det underliggende Linux-filsystem.\nPå normale Raspberry PIs er pinsene, der bruges til SDL og SCL, normalt '2 for SDA' og '3 for SDL'\n\nHvis du ikke kan se de pins, du vil bruge her, skal du sørge for, at du ikke allerede har tildelt dem til noget andet.\n\n<b>Felterne for SDL- og SCL-pinnen påvirker ikke den valgte bus.</b>",
      configuration_warning: '<b><u>ADVARSEL!!!:</b></u> Aktivering og deaktivering af I2C-bussen vil udløse unix-kommandoer, der konfigurerer I2C-bussen. Sørg for, at SDA og SCL er valgt korrekt. Ellers kan det ske, at pins er i brug som normale pins og I2C-pins på samme tid. Dette vil få applikationen til at gå ned!'
    },
    pump_setup: {
      headline: 'Pumpeopsætningsassistent',
      delete_btn_label: 'Slet pumpe',
      name: {
        handle: 'Håndtag',
        headline: 'Hvordan skal din pumpe kaldes?',
        pump_identifier_label: 'Pumpeidentifikator'
      },
      hw_pins: {
        handle: 'Hardware pins',
        headline: 'Vælg de pins, der styrer pumpen'
      },
      calibration: {
        handle: 'Kalibrer',
        headline: 'Kalibrer din pumpe',
        tube_capacity_label: 'Slangekapacitet (i ml)',
        tube_capacity_desc: 'Slangekapaciteten bestemmer, hvor meget væske der er nødvendigt for at fylde slangen, der forbinder væskebeholderen med uddelerdelen af din cocktailmaskine. Denne metrik bruges til nøjagtigt at fylde dine slanger med væske, før der faktisk produceres en ny drink. Den bruges også til at tømme dine slanger (pumpe væsken tilbage i beholderen), hvis maskinen ikke har været brugt i et stykke tid.',
        motor_tester: {
          headline: 'Motortester',
          disable_reason_parameter_missing: 'Krævet pumpe-konfigurationsparameter mangler!',
          stepper_desc: "Her kan du teste din motor og beregne antallet af trin, motoren skal tage for at pumpe en cl. Du kan køre testerne i to tilstande: 'Væske': Fortæl motoren, hvor meget væske den skal pumpe. 'Trin': Fortæl motoren, hvor mange trin den skal tage. Testeren bruges til at kontrollere og finjustere din konfiguration. Du kan også lade testeren beregne antallet af trin, som motoren skal tage for at pumpe en cl. Til dette skal du måle mængden af væske (i ml), som pumpen pumpede under din test. Du kan bruge en vægt til dette. Sørg også for, at dine slanger er fyldt med væske, da testeren ikke tager højde for tomme slanger eller luftbobler! Efterfølgende åbnes en boks, hvor du kan indtaste dine målinger. Testeren vil derefter rette konfigurationen i henhold til dine målinger.",
          dc_desc: "Her kan du teste din motor og beregne den tid, det tager motoren at pumpe en cl. Du kan køre testeren i to tilstande: 'Væske': Fortæl motoren, hvor meget væske den skal pumpe. 'Tid': Fortæl motoren, hvor mange trin den skal tage. Testeren bruges til at kontrollere og finjustere din konfiguration. Du kan også lade testeren beregne mængden af tid, som motoren skal køre for at pumpe en cl. Til dette skal du måle mængden af væske (i ml), som pumpen pumpede under din test. Du kan bruge en vægt til dette. Sørg også for, at dine slanger er fyldt med væske, da testeren ikke tager højde for tomme slanger eller luftbobler! Efterfølgende åbnes en boks, hvor du kan indtaste dine målinger. Testeren vil derefter rette konfigurationen i henhold til dine målinger!"
        }
      },
      state: {
        handle: 'Tilstand',
        headline: 'Pumpetilstand',
        pumped_up_label: 'Pumpet op',
        pumped_up_desc: "'Pumpet op'-feltet indeholder oplysninger om fyldningstilstanden af slangene på en pumpe. Hvis slangene ikke er fyldt med væske, vil maskinen fylde dem, før der produceres en cocktail. Dette felt bruges også til at finde ud af, fra hvilke pumper væsken skal pumpes tilbage i beholderen, hvis maskinen ikke er blevet brugt i et stykke tid.",
        filling_level_label: 'Nuværende fyldningsniveau',
        filling_level_desc: 'Det nuværende fyldningsniveau for beholderen, der er forbundet med pumpen. Dette felt bruges til at kontrollere, om der stadig er nok væske tilbage til at producere en cocktail af en bestemt størrelse.',
        ingredient_label: 'Nuværende ingrediens',
        ingredient_desc: 'Valgfri: Ingrediensen, der i øjeblikket er forbundet med pumpen.'
      },
      caption_complete: 'Komplet',
      caption_optional: 'Valgfri',
      continue_step_btn_label: 'Fortsæt',
      go_back_step_btn_label: 'Tilbage',
      finish_setup_btn_label: 'Afslut',
      delete_dialog: {
        headline: 'Slet {name}?',
        yes_btn_label: 'Slet'
      },
      notifications: {
        pump_deleted: '{name} slettet!'
      }
    },
    collection: {
      modify_recipes: 'Rediger opskrifter',
      stop_modify_recipes: 'Stop redigering af opskrifter',
      delete: 'Slet samling',
      add_recipe: 'Tilføj opskrift',
      delete_dialog: {
        ok_btn_label: 'Slet',
        headline: "Slet samling '{name}'?"
      },
      form: {
        name: 'Navn',
        image: 'Billede',
        remove_img: 'Fjern eksisterende billede',
        desc: 'Beskrivelse',
        edit_btn_label: 'Rediger',
        save_btn_label: 'Gem',
        abort_btn_label: 'Afbryd'
      }
    },
    notifications: {
      recipe_added: 'Opskrift tilføjet succesfuldt',
      recipe_removed: 'Opskrift fjernet succesfuldt',
      collection_updated: 'Samling opdateret succesfuldt'
    }
  },
  component: {
    show_img_dialog: {
      close_btn_label: 'Luk'
    },
    simple_recipes_filter_drawer: {
      headline: 'Filter',
      open_btn_label: 'Filter',
      name_field_label: 'Navn',
      contains_ingredients_field_label: 'Indeholder ingredienser',
      order_by_selector_label: 'Sortér efter',
      fabricable_box: {
        headline: 'Fremstillelig:',
        show_all: 'Vis alle',
        show_all_desc: 'Viser alle opskrifter',
        fabricable_owned: 'Fremstillelig med ejede ingredienser',
        fabricable_owned_desc: 'Viser kun opskrifter, der kan produceres med ejede ingredienser',
        fabricable_auto: 'Fremstillelig fuldautomatisk',
        fabricable_auto_desc: 'Viser kun opskrifter, der kan produceres fuldautomatisk'
      },
      search_btn_label: 'Søg',
      reset_btn_label: 'Nulstil'
    },
    simple_collection_card: {
      name: '{name}',
      nr_cocktails: '{nr} cocktail(s)',
      desc: '{desc}',
      owner: 'af {owner}'
    },
    recipe_search_list: {
      loading: 'Indlæser...'
    },
    recipe_list: {
      no_recipes_found: 'Ingen opskrifter fundet!'
    },
    recipe_search_filter_card: {
      headline: 'Søgemuligheder',
      expert_search_label: 'Ekspertsøgning',
      cocktail_name_field_label: 'Søg',
      fabricable_box: {
        headline: 'Fremstillelig:',
        show_all: 'Vis alle',
        fabricable_owned: 'Fremstillelig med ejede ingredienser',
        fabricable_auto: 'Fremstillelig fuldautomatisk'
      },
      contains_ingredient_field_label: 'Indeholder ingredienser',
      order_by_selector_label: 'Sortér efter',
      reset_btn_label: 'Nulstil filtre'
    },
    recipe_card: {
      owner_name: 'af {name}',
      ingredient_add_manually_tooltip: 'tilføj manuelt',
      ingredient_not_owned_tooltip: 'ejes ikke'
    },
    ingredient_list: {
      headline: 'Produktionstrin',
      no_steps_added: 'Ingen produktionstrin tilføjet!',
      alc_content: '{nr}% alkoholindhold',
      alc_content_range: '{min} - {max}% alkoholindhold',
      manual_instruction: 'Manuel instruktion:',
      tag_boostable: 'Forstærkbar',
      tag_unscaled: 'Uskaleret',
      edit_dialog: {
        edit_headline: 'Rediger Produktionstrin',
        add_headline: 'Tilføj Produktionstrin',
        save_btn_label: 'Gem',
        abort_btn_label: 'Afbryd'
      }
    },
    prod_step_editor: {
      tab_ingredient: 'Ingrediens',
      tab_instruction: 'Instruktion'
    },
    prod_step_editor_ingredient: {
      amount: 'Mængde',
      amount_in: 'Mængde (i {metric})',
      scale_label: 'Skalér med volumen',
      boostable_label: 'Forstærkbar'
    },
    prod_step_editor_instruction: {
      instruction_label: 'Instruktion'
    },
    ingredient_selector: {
      default_label: 'Ingrediens',
      alc_content: '{nr}% alkoholindhold',
      alc_content_range: '{min} - {min}% alkoholindhold'
    },
    make_cocktail_dialog: {
      headline: 'Bestil Cocktail',
      order_btn_label: 'LAV COCKTAIL ({nr} ml)'
    },
    make_cocktail_amount_to_produce: {
      glass_selector_label: 'Glas',
      amount_to_produce_label: 'Mængde der skal produceres'
    },
    make_cocktail_group_replacements: {
      fulfilled_msg: 'Alle ingrediensgrupper er erstattet med konkrete ingredienser!',
      not_fulfilled_msg: 'Følgende ingrediensgrupper skal have tildelt rigtige eksisterende ingredienser:',
      card: {
        prod_step_label: 'Produktionstrin',
        ingredient_group_label: 'Ingrediensgruppe',
        replacement_label: 'Erstatning',
        tags: {
          in_bar: 'i baren',
          not_in_bar: 'ikke i baren',
          automatically_addable: 'Automatisk tilføjelig'
        }
      }
    },
    make_cocktail_add_manually: {
      fulfilled_msg: 'Alle ingredienser tildelt pumper! Cocktail kan produceres fuldautomatisk!',
      not_fulfilled_msg: 'Følgende ingredienser skal tilføjes manuelt eller er ikke tildelt pumper. Du vil blive bedt om at tilføje dem under produktionsforløbet:',
      tags: {
        in_bar: 'i baren',
        not_in_bar: 'ikke i baren'
      }
    },
    make_cocktail_occupied: {
      fulfilled_msg: 'Maskinen er ikke optaget!',
      occupied_cocktail_msg: 'Maskinen optaget! En cocktail bliver tilberedt i øjeblikket!',
      occupied_pumps_msg: 'Maskinen optaget! En eller flere pumper bliver rengjort/pumpet op i øjeblikket!'
    },
    make_cocktail_insufficient_ingredients: {
      fulfilled_msg: 'Følgende ingredienser vil blive forbrugt:',
      not_fulfilled_msg: 'Kan ikke lave cocktail! Nogle pumper har ikke nok væske tilbage:'
    },
    make_cocktail_customizer: {
      headline: 'Bestillingspersonalisering',
      headline_boosting: 'Forstærkning:',
      boosting_desc: 'Øger (eller reducerer) ml af rapporterede forstærkbare ingredienser i basisopskriften. (Normalt spiritus) Ikke-forstærkbare ingredienser reduceres (eller øges). Mængden af dispenserede væsker forbliver den samme!',
      headline_additional_ingredients: 'Yderligere ingredienser:',
      additional_ingredients_desc: 'Ingredienser vil blive tilføjet som sidste produktionstrin. Mængden af dispenserede væsker øges med mængden af bestilte yderligere ingredienser.',
      add_new_ingredient_btn_label: 'Tilføj ny ingrediens',
      add_new_ingredient_headline: 'Tilføj ingrediens',
      recipe_not_boostable: 'Opskrift ikke forstærkbar!'
    },
    make_cocktail_pump_editor: {
      headline: 'Pumpe-Layout',
      pump_table: {
        required_ingredient: 'Krævet ingrediens',
        columns: {
          nr: 'Nr',
          ingredient: 'Nuværende Ingrediens',
          filling_level: 'Resterende fyldningsniveau',
          pumped_up: 'Pumpet Op',
          state: 'Tilstand',
          actions: 'Handlinger'
        },
        refill_btn_label: 'Fyld',
        state_ok: 'OK',
        state_incomplete: 'Ukomplet'
      }
    },
    pump_up_btn: {
      tooltip_up: 'Pump op',
      tooltip_back: 'Pump tilbage'
    },
    pump_turn_on_btn: {
      tooltip_on: 'Tænd',
      tooltip_off: 'Sluk'
    },
    deleteWarning: {
      delete_btn_label: 'Slet',
      abort_btn_label: 'Afbryd',
      success_notification: 'Slettet succesfuldt!',
      nothing_selected: 'Intet valgt!'
    },
    editDialog: {
      save_btn_label: 'Gem',
      abort_btn_label: 'Afbryd'
    },
    ingredientGroupForm: {
      name: 'Navn',
      parent_group: 'Forældergruppe'
    },
    ingredient_group_mgmt: {
      create_btn_label: 'Opret gruppe',
      refresh_btn_label: 'Opdater',
      group_table: {
        search_field_label: 'Søg',
        columns: {
          group: 'Gruppe',
          alc_content: 'Alkoholindhold',
          parent_group: 'Forældergruppe',
          actions: 'Handlinger'
        },
        edit_btn_tooltip: 'Rediger',
        delete_btn_tooltip: 'Slet',
        delete_dialog: {
          headline: 'Slet gruppe?',
          warning: 'Dette fjerner også den valgte gruppe fra alle tilknyttede opskrifter!'
        }
      },
      edit_headline: 'Rediger gruppe',
      create_headline: 'Opret gruppe',
      notifications: {
        group_created: 'Gruppe oprettet succesfuldt',
        group_updated: 'Gruppe opdateret succesfuldt'
      }
    },
    ingredient_mgmt: {
      create_btn_label: 'Opret ingrediens',
      refresh_btn_label: 'Opdater',
      ingredient_table: {
        search_field_label: 'Søg',
        columns: {
          ingredient: 'Ingrediens',
          type: 'Type',
          alc_content: 'Alkoholindhold',
          bottle_size: 'Flaskestørrelse',
          unit: 'Enhed',
          pump_time_multiplier: 'Pumpe tidsmultiplikator',
          parent_group: 'Forældergruppe',
          has_image: 'Billede',
          actions: 'Handlinger'
        },
        has_img_col: {
          no_label: 'Nej',
          yes_btn_label: 'Ja / Vis'
        },
        edit_btn_tooltip: 'Rediger',
        delete_btn_tooltip: 'Slet'
      },
      edit_dialog: {
        headline_create: 'Opret ingrediens',
        headline_edit: 'Rediger ingrediens'
      },
      delete_dialog: {
        headline: 'Slet Ingrediens?',
        warning: 'Dette fjerner også den valgte gruppe fra alle tilknyttede opskrifter!'
      },
      notifications: {
        ingredient_created: 'Ingrediens oprettet succesfuldt',
        ingredient_updated: 'Ingrediens opdateret succesfuldt'
      }
    },
    ingredient_form: {
      name: 'Navn',
      alc_content: 'Alkoholindhold',
      tab_automated: 'Automatiseret',
      tab_manual: 'Manuel',
      parent_group: 'Forældergruppe',
      image: 'Billede',
      remove_img: 'Fjern eksisterende billede',
      bottle_size: 'Flaskestørrelse',
      pump_time_multiplier: 'Pumpe tidsmultiplikator',
      unit: 'Enhed',
      units: {
        gram: 'gram (g)',
        milliliter: 'milliliter (ml)',
        piece: 'stk',
        teaspoon: 'teskefuld(er)',
        tablespoon: 'spiseskefuld(er)'
      }
    },
    gpio_expander_expansion_item: {
      caption_local: 'Board: Lokal, Anvendelse: {pinsUsed}/{pinsMax}',
      caption_i2c: 'Adresse: {addr}, Board: {board}, Anvendelse: {pinsUsed}/{pinsMax}'
    },
    pump_mgmt: {
      headline: 'Pumpestyring',
      add_btn_label: 'Tilføj',
      start_all_btn_label: 'Start alle',
      stop_all_btn_label: 'Stop alle',
      no_pumps_found: 'Ingen pumper fundet!',
      notifications: {
        all_stopped: 'Alle pumper stoppet!',
        all_started: 'Alle pumper startet!'
      }
    },
    pump_setup_type_selector: {
      headline: 'Hvilken type pumpe vil du tilføje?',
      dc_pump: 'DC Pumpe',
      stepper_pump: 'Stepper Pumpe'
    },
    pump_card: {
      dc_pump: 'DC Pumpe',
      stepper_pump: 'Stepper Pumpe',
      option_missing: '-- mangler --',
      no_ingredient_placeholder: 'Ingen',
      fallack_name: 'Pumpe #{id}',
      attr: {
        ingredient: 'Ingrediens',
        filling_level: 'fyldningsniveau',
        time_per_cl: 'Tid pr. cL',
        enable_pin: 'Enable pin',
        running_state: 'Kørende tilstand',
        tube_capacity: 'Slangekapacitet',
        steps_per_cl: 'Steps pr. cL',
        acceleration: 'Acceleration',
        max_steps_per_second: 'Maks. steps pr. sekund',
        step_pin: 'Step pin'
      },
      pumpStates: {
        ready: 'Klar',
        incomplete: 'Ukomplet',
        running: 'Kører'
      },
      pumpUpStates: {
        pumped_up: 'Primet',
        pumped_down: 'Ikke primet'
      },
      notifications: {
        pump_started: '{name} startet!',
        pump_stopped: '{name} stoppet!'
      }
    },
    reverse_pump_settings: {
      headline: 'Omvendt pumpning',
      form: {
        timer_options: {
          in_minutes: '{nr} Minutter',
          never: 'Aldrig'
        },
        enable_label: 'Aktivér omvendt pumpning',
        vd_pin_headline: 'Voltage director pin',
        vd_pin_label: 'Director-Pin',
        overshoot_label: 'Overshoot',
        overshoot_hint: 'Hvor kraftigt skal antallet af mL overskydes ved pumpning tilbage?',
        auto_pump_back_timer_label: 'Inaktiv tid indtil automatisk pumpning tilbage',
        save_btn_label: 'Gem'
      },
      notifications: {
        updated: 'Indstillinger opdateret!'
      }
    },
    pump_tester: {
      unknown_job_running: 'Ukendt job kører!',
      default_disable_reason: 'Deaktiveret!',
      ref_metric: 'Metrik:',
      metrics: {
        liquid: 'Væske',
        liquid_pumped: 'Pumpet væske:',
        liquid_run_val_field: 'Ml der skal pumpes',
        steps: 'Trin',
        steps_made: 'Trin taget:',
        steps_run_val_field: 'Trin der skal løbes',
        time: 'Tid',
        time_taken: 'Taget tid:',
        time_run_val_field: 'Ms der skal køres',
        unknown_run_metric: 'Ukendt metrik'
      },
      true_liquid_pumped_field: 'Faktisk ml pumpet',
      apply_per_cl_metric_value_btn_label: 'Anvend',
      run_btn_label: 'Kør',
      stop_btn_label: 'Stop'
    },
    pump_setup_dc_hw_pins: {
      control_pin_label: 'Kontrol Pin',
      control_pin_desc: "En DC-motor kan tændes og slukkes ved at forbinde den til og frakoble den fra en strømkilde. Dette gøres normalt ved hjælp af et relæ. Relæet åbner og lukker den elektroniske kreds, hvori motoren er tilsluttet. 'BCM-Pin'-feltet indeholder BCM-nummeret på den pin, der styrer relæet.<br><br><b>Vigtigt:</b> For det lokale board, der tilhører Raspberry Pi, svarer pinnenumrene ikke nødvendigvis til GPIO-numre, men BCM-numre. BCM henviser til 'Broadcom SOC channel'-nummeret, som er nummereringen inde i chippen, der bruges på Raspberry Pi. Disse numre ændredes mellem boardversionerne. Dette link kan hjælpe:",
      power_state_desc: 'Afhængigt af vores opsætning kan motoren køre enten hvis GPIO-pinnen, der styrer pumpen, er sat til høj eller lav. Vælg venligst pin-tilstanden, hvor din pumpe ville køre i din konfiguration.',
      power_state_label: 'Strømtilstand',
      power_state_options: {
        high: 'Høj',
        low: 'Lav'
      }
    },
    pump_setup_dc_calibration: {
      time_per_cl_pin_label: 'Tid pr. cl i ms',
      time_per_cl_pin_desc: '"Tid pr. cl i ms" bestemmer, hvor mange millisekunder (ms) pumpen skal køre for at pumpe et centiliter (cl). Denne værdi bruges til at bestemme, hvor længe pumpen skal køre for at pumpe den ønskede mængde væske fra flasken.'
    },
    pump_setup_stepper_hw_pins: {
      step_pin_label: '(lokal) Trin Pin',
      enable_pin_label: 'Aktiver Pin',
      pin_desc: 'En stepper-motor-driver har normalt tre vigtige pins, der bruges til at styre motoren.\n' +
        '<ul>' +
        '        <li>' +
        '          Trin-pinnen, som modtager et pulssignal for hvert trin, motoren skal tage.' +
        '        </li>' +
        '        <li>' +
        '          Aktiver-pinnen. Denne pin bestemmer, om motoren skal være energiseret og dermed aktivt holde sin nuværende position eller ej.' +
        '        </li>' +
        '        <li>' +
        '          Retningspinnen. Den afgør, hvilken retning motoren tager. Retningen, som motoren kører til, bestemmes af en enkelt pin, der styrer retningen for alle motorer. Denne pin defineres globalt og er ikke en del af denne opsætning! Byg venligst din maskine på en måde, der forbinder denne pin med retninglogikken for alle dine motorer.' +
        '        </li>' +
        '        <li>' +
        '          Din motor-driver har sandsynligvis også flere pins (trinopløsning/søvn/...). Konfigurer disse statisk i hardwaren!' +
        '        </li>' +
        '      </ul>' +
        '<b>Vigtigt:</b> For det lokale board, der tilhører Raspberry Pi, svarer pinnenumrene ikke nødvendigvis til GPIO-numre, men BCM-numre. BCM henviser til “Broadcom SOC channel”-nummeret, som er nummereringen inde i chippen, der bruges på Raspberry Pi. Disse numre ændredes mellem boardversionerne. Dette link kan hjælpe:'
    },
    pump_setup_stepper_calibration: {
      acceleration_label: 'Acceleration',
      acceleration_desc: 'Acceleration-feltet bestemmer, hvor hurtigt motoren skal accelerere eller decelerere. Hvis accelerationen er for høj, kan motoren springe trin over, når den accelererer, eller tage for mange trin, når den decelererer. Accelerationen er angivet i trin pr. sekund pr. sekund.',
      max_steps_per_second_label: 'Maks. trin pr. sekund',
      max_steps_per_second_desc: '<p>' +
        '        "Maks. trin pr. sekund"-feltet bestemmer, hvor hurtigt motoren skal spinde ved maks. En revolution er normalt opdelt i 200 trin. Dette kan variere afhængigt af motoren og motor-driver-indstillingerne.' +
        '        Hvis værdien er for høj, kan motoren muligvis ikke følge med og kan springe trin over eller endda slet ikke køre. ' +
        '        Hvis værdien er for lav, kører motoren langsommere end nødvendigt.<br>' +
        '      </p>' +
        '      <p>' +
        '        Reglen er:' +
        '      </p>' +
        '      <ul>' +
        '        <li>højere = hurtigere motor</li>' +
        '        <li>lavere = langsommere motor</li>' +
        '      </ul>',
      steps_per_cl_label: 'Trin pr. cl',
      steps_per_cl_desc: 'Dette felt bestemmer, hvor mange trin motoren skal tage for at pumpe en cl.'
    },
    glass_form: {
      name: 'Navn',
      size: 'Størrelse',
      default_checkbox: 'Standard',
      use_for_single_ingredients_checkbox: 'Brug til enkelte ingredienser'
    },
    event_action_editor_form: {
      comment_label: 'Kommentar',
      execution_groups: {
        label: 'Udførelsesgrupper',
        write_to_create: 'Skriv for at oprette ny gruppe...',
        create_new: '<b>Opret ny:</b> {group}'
      },
      trigger_label: 'Udløser',
      action: {
        label: 'Handling',
        select_action: 'Vælg handling for at se muligheder...',
        options: {
          call_url: 'Ring URL',
          audio: 'Afspil lydfil',
          python: 'Udfør python script',
          nothing: 'Gør ingenting'
        },
        call_url: {
          request_method_label: 'Anmodningsmetode',
          url_label: 'URL'
        },
        audio: {
          file_label: 'Lydfil (maks. 20 MB)',
          volume: 'Lydstyrke {nr}%',
          output_device_label: 'Output-enhed',
          on_repeat_label: 'På gentagelse'
        },
        python: {
          desc: 'Udfører en python-fil. Docker-billedet bruger python 3. Du kan også se konsoloutput af dit program i realtid.',
          install_lib_tip: 'Installer nye biblioteker ved at bruge: <b>pip3 install &#60;bibliotek&#62;</b>',
          fetching_libs: 'Henter biblioteker',
          lib_list_lib: 'Bibliotek',
          lib_list_version: 'Version',
          no_libs_found: 'Ingen biblioteker installeret',
          python_file_label: 'Python (maks. 20 MB)'
        },
        nothing: {
          desc: 'Denne handling gør intet. Men den vil afbryde andre igangværende handlinger, der ikke deler udførelsesgrupper med den.'
        }
      }
    },
    circular_cocktail_progress: {
      headline: 'Nuværende fremstillet cocktail',
      no_cocktail_msg: 'I øjeblikket bliver der ikke fremstillet nogen cocktail!<br>' +
        'Gå til "Mine opskrifter" eller "Offentlige opskrifter" for at bestille en.',
      close_btn_label: 'Luk'
    },
    cocktail_production_manual_step_card: {
      continue_btn_label: 'Fortsæt',
      add_ingredient_headline: 'Tilføj venligst følgende ingredienser manuelt og klik på "fortsæt":'
    },
    donation_disclaimer: {
      headline: 'Din støtte er nødvendig!',
      headline_caption: 'Rul ned for at lukke',
      donate_paypal_btn_label: 'Donér via PayPal',
      donate_github_btn_label: 'Donér via GitHub sponsorer',
      headline_2: 'Denne software er gratis, men donationer værdsættes!',
      introduction: 'Denne software er gratis, men det har stadig taget og tager meget tid at udvikle og vedligeholde den. Udvikleren af CocktailPi (mig) udvikler den i sin fritid. Jeg er også en studerende, der ikke har en reel indkomst endnu.',
      main_text: '<p>Her er nogle flere grunde til, hvorfor du bør donere:</p>' +
        '            <ul>\n' +
        '              <li>\n' +
        '                Brugere af denne software bruger normalt store summer penge på hardware (en Raspberry Pi, pumper, et kabinet,\n' +
        '                ...).\n' +
        '                Alligevel ville denne hardware ikke fungere, hvis CocktailPi-softwaren ikke eksisterede.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                Du tror måske, at nogen andre vil donere, men desværre tror de det også. Før jeg tilføjede denne\n' +
        '                ansvarsfraskrivelse, modtog jeg omkring 50 € i donationer på to år\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                Jeg får normalt ingen feedback for softwaren. Den indsamler ingen data. Jeg har ingen idé om, hvor mange\n' +
        '                mennesker derude, der bruger den.\n' +
        '                En donation og også "stjerner" på GitHub giver mig positiv feedback og motiverer mig til at fortsætte med at arbejde på\n' +
        '                CocktailPi.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                Udvikling af denne software forårsager omkostninger. Jeg som udvikler køber ofte hardware, blot for at teste\n' +
        '                om det ville fungere med enheden og give mening.\n' +
        '              </li>\n' +
        '            </ul>' +
        '            <p>\n' +
        '              Donation er helt frivillig. Mens softwaren forbliver gratis at bruge, viser dit bidrag\n' +
        '              din påskønnelse for det hårde arbejde og dedikation, der krævedes for at udvikle denne software.\n' +
        '              En donation motiverer mig også til at fortsætte med at forfine og udvide softwarens kapaciteter.\n' +
        '            </p>\n' +
        '            <p>\n' +
        '              Du kan donere ved hjælp af GitHub Sponsors eller Paypal. Du kan vælge et hvilket som helst beløb, som du mener, at softwaren\n' +
        '              er værd for dig.\n' +
        '              Du kan også foretage månedlige donationer, hvis du ønsker at støtte mig og mit arbejde over en periode.\n' +
        '            </p>',
      action_box: {
        donated: {
          headline: 'Tak for din donation. Du gjorde en forskel!',
          caption: 'Dette sker mindre ofte, end du måske tror. Mange tak!',
          close_btn: 'Fantastisk menneske-knap (Luk ansvarsfraskrivelse)',
          revert_btn: 'Jeg donerede ikke'
        },
        not_donated: {
          headline: 'Mange tak!',
          donated_btn: 'Jeg har doneret',
          close_btn: 'Nej, påmind mig senere'
        },
        lying_is_no_nice: {
          headline: 'Vær opmærksom på, at det ikke er pænt at lyve.',
          confirm_btn: 'Bekræft',
          checkbox: 'Jeg er ikke en løgner',
          go_back: 'Gå tilbage'
        }
      }
    },
    settings_appearance: {
      language: 'Sprog',
      save_btn_label: 'Gem',
      notifications: {
        settings_updated: 'Indstillinger opdateret!'
      },
      colors: {
        interface: {
          headline: 'Grænsefladefarver',
          header: 'Header',
          sidebar: 'Sidebar',
          background: 'Baggrund',
          btn_primary: 'Knap / Primær',
          btn_navigation_active: 'Knap / Navigation aktiv',
          card_header: 'Kort / Header',
          card_body: 'Kort / Body',
          card_item_group: 'Kort / Varegruppe'
        },
        simple_view: {
          headline: 'Simpel visningsfarver',
          header: 'Header + Footer',
          sidebar: 'Sidebar',
          background: 'Baggrund',
          btn_primary: 'Knap / Primær',
          btn_navigation: 'Knap / Navigation',
          btn_navigation_active: 'Knap / Navigation aktiv',
          cocktail_progress: 'Cocktail fremskridtsbanner',
          card: 'Kort'
        }
      }
    }
  },
  constants: {
    recipe_order_options: {
      name_asc: 'Navn stigende',
      name_desc: 'Navn faldende',
      last_update_asc: 'Sidste opdatering',
      last_update_desc: 'Mindst opdatering'
    },
    event_action_trigger_display_names: {
      cocktail_prod_started: 'Cocktailproduktion startet',
      cocktail_prod_manual_interaction_requested: 'Manuel interaktion under cocktailproduktion anmodet',
      cocktail_prod_manual_interaction_completed: 'Manuel interaktion under cocktailproduktion afsluttet',
      cocktail_prod_finished: 'Cocktailproduktion afsluttet',
      cocktail_prod_canceled: 'Cocktailproduktion aflyst',
      application_started: 'Applikation startet'
    }
  }
}
