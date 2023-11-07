export default {
  errors: {
    field_required: 'Pflichtfeld',
    not_valid_url: 'Keine gültige URL',
    min_letters: 'Mindestens {nr} Buchstaben',
    max_letters: 'Maximal {nr} Buchstaben',
    min_metric: 'Mindestens {nr} {metric}',
    max_metric: 'Maximal {nr} {metric}',
    positive: 'Muss positiv sein'
  },
  header: {
    machine_name: 'CocktailMaker',
    profile: {
      profile_btn_label: 'Profil',
      logout_btn_label: 'Abmelden'
    }
  },
  simple_header: {
    machine_name: 'CocktailMaker',
    leave_sv_btn_label: 'Touchansicht verlassen',
    go_to_cocktail_progress_btn_label: 'Details >>',
    leave_sv_dialog: {
      headline: 'Touchansicht verlassen?',
      yes_btn_label: 'Ja',
      no_btn_label: 'Nein'
    }
  },
  simple_footer: {
    collections_btn_label: 'Listen',
    recipes_btn_label: 'Rezepte',
    ingredients_btn_label: 'Zutaten'
  },
  common: {
    pump_fallback_name: 'Pumpe #{id}'
  },
  layout: {
    full_layout: {
      sidebar: {
        me: {
          label: 'Ich',
          dashboard: 'Dashboard',
          simple_view: 'Touchansicht',
          bar: 'Bar',
          collections: 'Listen',
          my_recipes: 'Meine Rezepte'
        },
        cocktails: {
          label: 'Cocktails',
          category_all: 'Alle',
          ingredients: 'Zutaten'
        },
        administration: {
          label: 'Verwaltung',
          user_mgmt: 'Benutzer',
          ingredient_mgmt: 'Zutaten',
          category_mgmt: 'Kategorien',
          gpio_mgmt: 'GPIO',
          pump_mgmt: 'Pumpen',
          glass_mgmt: 'Gläser',
          event_mgmt: 'Events',
          system_mgmt: 'System'
        }
      },
      donate_btn_label: 'Spenden'
    }
  },
  page: {
    login: {
      login_btn_label: 'Anmelden',
      headline: 'Login',
      password_field_label: 'Passwort',
      username_field_label: 'Benutzername',
      remember_me_label: 'Angemeldet bleiben',
      errors: {
        credentials_invalid: 'Benutzername oder Passwort falsch!',
        server_unreachable: 'Server nicht erreichbar!'
      }
    },
    bar: {
      headline: 'Eigene Zutaten',
      add_btn_label: 'Zutat hinzufügen',
      refresh_btn_label: 'Aktualisieren',
      owned_table: {
        nr_ingredients_owned: '{nrIngredients} Zutat(en) insgesamt',
        no_data_msg: 'Keine Zutaten hinzugefügt',
        columns: {
          ingredient: 'Zutat',
          alc_content: 'Alkoholgehalt',
          actions: 'Aktionen'
        },
        delete_btn_tooltip: 'Löschen'
      },
      add_dialog: {
        headline: 'Zutat hinzufügen',
        ingredient_selector_label: 'Zutat',
        abort_btn_label: 'Abbrechen',
        save_btn_label: 'Speichern',
        required_error: 'Erforderlich'
      },
      notifications: {
        ingredient_added: 'Zutat erfolgreich hinzugefügt',
        ingredient_removed: 'Zutat erfolgreich entfernt'
      }
    },
    404: {
      go_home_btn_label: 'Zurück zur Startseite',
      message: 'Ups. Hier ist nichts...'
    },
    collections: {
      headline: 'Meine Listen',
      add_btn_label: 'Liste erstellen',
      refresh_btn_label: 'Aktualisieren',
      no_collections_msg: 'Keine Listen gefunden!',
      create_dialog: {
        headline: 'Liste erstellen',
        name_field_label: 'Name',
        abort_btn_label: 'Abbrechen',
        create_btn_label: 'Erstellen'
      }
    },
    recipes: {
      my_recipes_headline: 'Meine Rezepte',
      all_recipes_headline: 'Rezepte',
      create_recipe_btn_label: 'Rezept erstellen',
      refresh_btn_label: 'Aktualisieren'
    },
    ingredient_recipes: {
      headline: 'Pumpbare Zutaten',
      no_ingredients_available: 'Keine Zutaten angeschlossene!',
      recipe: {
        ml_left: '{nr} ml übrig',
        alc_content: '{nr}% Alkohol'
      }
    },
    user_mgmt: {
      headline: 'Benutzerverwaltung',
      create_user_btn_label: 'Benutzer erstellen',
      refresh_users_btn_label: 'Aktualisieren',
      edit_user_btn_tooltip: 'Bearbeiten',
      delete_user_btn_tooltip: 'Löschen',
      user_table: {
        nr_users: '{nr} Benutzer insgesamt',
        columns: {
          username: 'Benutzername',
          active: 'Aktiv',
          role: 'Rolle',
          actions: 'Aktionen'
        }
      },
      delete_dialog: {
        headline: 'Der folgende Benutzer wird gelöscht:'
      }
    },
    ingredient_mgmt: {
      headline: 'Zutatenverwaltung',
      help_dialog: {
        ingredients: {
          headline: 'Zutaten:',
          text: 'Zutaten sind konkrete Zutaten, die direkt zu einem Glas hinzugefügt werden können und im Supermarkt gekauft werden können. Zutaten können in der Regel einer Pumpe zugeordnet werden.'
        },
        groups: {
          headline: 'Zutatengruppen:',
          text: "Zutatengruppen sind abstrakte Gruppen, die aus mehreren Zutaten oder anderen Zutatengruppen bestehen können. Rezepte können Zutatengruppen enthalten. Bevor ein solches Rezept bestellt werden kann, muss der Benutzer diese Zutatengruppe durch eine konkrete Zutat ersetzen, die zu dieser Gruppe gehört. Die Software erledigt dies automatisch für den Benutzer, wenn eine solche Zutat einer Pumpe zugeordnet wurde oder als 'Bar'-Zutat markiert ist. Zutaten auf Pumpen haben Vorrang.\n\nBeispiel: Sie haben eine spezifische Zutat namens 'Whisky 08/15'. Dieser Whisky ist in der Zutatengruppe 'Bourbon' kategorisiert. Die Gruppe 'Bourbon' wiederum fällt unter die allgemeinere Kategorie 'Whisky'. Es gibt eine zusätzliche Kindzutatengruppe, die mit 'Whisky' verbunden ist, bekannt als 'Scotch'.\n\nSie haben 'Whisky 08/15' in Ihrem Bar-Inventar. Aufgrund der hierarchischen Struktur erkennt CocktailMaker nun, dass 'Whisky 08/15' nicht nur in Rezepten verwendet werden kann, die diese spezielle Zutat erfordern, sondern auch in verschiedenen Rezepten, die nach einen beliebigen 'Bourbon' oder 'Whisky' verlangen. Es ist jedoch wichtig zu beachten, dass 'Whisky 08/15' nicht als Ersatz für Rezepte dienen kann, die explizit 'Scotch' oder andere spezifische Zutaten beinhalten."
        },
        close_btn_label: 'Schließen'
      },
      ingredient_groups: {
        headline: 'Zutatengruppen',
        create_btn_label: 'Gruppe erstellen',
        refresh_btn_label: 'Aktualisieren',
        group_table: {
          search_field_label: 'Suche',
          columns: {
            group: 'Gruppe',
            alc_content: 'Alkoholgehalt',
            parent_group: 'Übergeordnete Gruppe',
            actions: 'Aktionen'
          },
          edit_btn_tooltip: 'Bearbeiten',
          delete_btn_tooltip: 'Löschen'
        }
      },
      ingredients: {
        headline: 'Zutaten',
        create_btn_label: 'Zutat erstellen',
        refresh_btn_label: 'Aktualisieren',
        ingredient_table: {
          search_field_label: 'Suche',
          columns: {
            ingredient: 'Zutat',
            type: 'Typ',
            alc_content: 'Alkoholgehalt',
            bottle_size: 'Flaschengröße',
            unit: 'Einheit',
            pump_time_multiplier: 'Pumpenzeitmultiplikator',
            parent_group: 'Übergeordnete Gruppe',
            actions: 'Aktionen'
          },
          edit_btn_tooltip: 'Bearbeiten',
          delete_btn_tooltip: 'Löschen'
        }
      }
    },
    category_mgmt: {
      headline: 'Kategorienverwaltung',
      create_btn_label: 'Kategorie erstellen',
      refresh_btn_label: 'Aktualisieren',
      no_data_msg: 'Keine Kategorien gefunden!',
      delete_dialog: {
        headline: 'Die folgende(n) Kategorie(n) werden gelöscht:',
        ok_btn_label: 'Löschen',
        abort_btn_label: 'Abbrechen'
      },
      category_table: {
        columns: {
          category: 'Kategorie',
          actions: 'Aktionen'
        },
        edit_btn_tooltip: 'Bearbeiten',
        delete_btn_tooltip: 'Löschen',
        nr_categories: '{nr} Kategorien insgesamt'
      },
      notifications: {
        category_updated: 'Kategorie erfolgreich aktualisiert',
        category_created: 'Kategorie erfolgreich erstellt',
        category_deleted: 'Kategorie erfolgreich gelöscht'
      },
      create_dialog: {
        headline_edit: 'Kategorie bearbeiten',
        headline_create: 'Kategorie erstellen',
        name_field_label: 'Name',
        save_btn_label: 'Speichern',
        abort_btn_label: 'Abbrechen'
      }
    },
    gpio_mgmt: {
      headline: 'GPIO-Verwaltung',
      status_box: {
        headline: 'Status',
        pin_box: {
          headline: 'Pin-Nutzung',
          pin_usage: 'Pin-Nutzung:',
          gpio_boards: 'GPIO-Platinen:'
        },
        i2c_box: {
          headline: 'I2C',
          configure_btn_label: 'Konfigurieren',
          status: 'Status:',
          sda_pin: 'SDA-Pin:',
          scl_pin: 'SCL-Pin:'
        }
      },
      local_gpio_box: {
        headline: 'Lokale GPIOs:',
        no_boards_found: 'Kein Board gefunden!'
      },
      i2c_expander_box: {
        add_btn_label: 'Hinzufügen',
        headline: 'I2C GPIO-Expander:',
        i2c_disabled: 'I2C deaktiviert!',
        no_expanders_found: 'Keine Expander gefunden!'
      },
      delete_dialog: {
        headline: '{boardName} löschen?',
        ok_btn_label: 'Löschen',
        abort_btn_label: 'Abbrechen'
      }
    },
    glass_mgmt: {
      headline: 'Gläserverwaltung',
      add_glass_btn_label: 'Glas erstellen',
      refresh_btn_label: 'Aktualisieren',
      glass_table: {
        columns: {
          name: 'Name',
          size: 'Größe',
          default: 'Standard',
          for_single_ingredients: 'Für einzelne Zutaten',
          actions: 'Aktionen'
        },
        edit_btn_tooltip: 'Bearbeiten',
        delete_btn_tooltip: 'Löschen',
        nr_glasses: '{nr} Gläser insgesamt'
      },
      notifications: {
        glass_updated: 'Glas erfolgreich aktualisiert',
        glass_created: 'Glas erfolgreich erstellt'
      },
      edit_dialog: {
        headline_create: 'Glas erstellen',
        headline_edit: 'Glas bearbeiten'
      },
      help_dialog: {
        headline: 'Gläser:',
        text: "Verschiedene Arten von Cocktails werden typischerweise in verschiedenen Glasstilen serviert, jeder mit seiner eigenen Größe. Beim Bestellen eines Cocktails haben Sie zwei Möglichkeiten: Sie können entweder die gewünschte Flüssigkeitsmenge für die Maschine angeben oder einen bestimmten Glasstyp auswählen. Entscheiden Sie sich für Letzteres, wird die Maschine automatisch eine Menge Flüssigkeit abgeben, die dem Volumen des ausgewählten Glases entspricht.\n\nDie Menge an Flüssigkeit, die die Maschine produzieren soll, wird immer vom Besteller gewählt. Es ist jedoch möglich, jedem Rezept ein Glas zuzuweisen, das standardmäßig ausgewählt wird.\n\nWenn für ein bestimmtes Rezept kein Standardglas ausgewählt wurde, wird die Software automatisch auf das vordefinierte 'Standard'-Glas zurückgreifen.\n\nEs ist möglich, einzelne Zutaten abzugeben. Das Standardglas für diese Bestellungen kann auch hier unter Verwendung der Eigenschaft 'Für einzelne Zutaten verwenden' ausgewählt werden."
      }
    },
    event_mgmt: {
      headline: 'Event-Verwaltung',
      add_btn_label: 'Aktion hinzufügen',
      refresh_btn_label: 'Aktualisieren',
      delete_btn_label: 'Ausgewählte Aktionen löschen',
      action_table: {
        columns: {
          trigger: 'Auslöser',
          description: 'Beschreibung',
          comment: 'Kommentar',
          groups: 'Ausführungsgruppen',
          status: 'Status',
          actions: 'Aktionen'
        },
        view_logs_btn_tooltip: 'Logs anzeigen',
        edit_btn_tooltip: 'Bearbeiten',
        delete_btn_tooltip: 'Löschen'
      },
      delete_dialog: {
        headline: 'Aktionen löschen?'
      },
      edit_dialog: {
        headline_create: 'Neue Aktion erstellen',
        headline_edit: 'Aktion bearbeiten'
      },
      notifications: {
        action_created: 'Aktion erfolgreich erstellt!',
        action_updated: 'Aktion erfolgreich aktualisiert!'
      }
    },
    system_mgmt: {
      system: {
        headline: 'System-Verwaltung',
        shutdown_btn_label: 'System herunterfahren',
        shutdown_dialog: {
          headline: 'Herunterfahren?',
          ok_btn_label: 'Herunterfahren',
          abort_btn_label: 'Abbrechen'
        }
      },
      default_filter: {
        headline: 'Standardfilter',
        enable_btn_label: 'Standardfilter aktivieren',
        fabricable: {
          headline: 'Herstellbar:',
          show_all: 'Alle anzeigen',
          fabricable_owned: 'Herstellbar mit vorhandenen Zutaten',
          fabricable_auto: 'Vollautomatisch herstellbar'
        },
        save_btn_label: 'Speichern',
        notifications: {
          settings_updated: 'Einstellungen aktualisiert!'
        }
      }
    },
    simple_collections: {
      headline: 'Listen',
      no_collections_msg: 'Keine Listen gefunden!'
    },
    simple_recipes: {
      headline: 'Rezepte',
      no_data_msg: 'Keine Rezepte gefunden!'
    },
    simple_ingredient_recipes: {
      headline: 'Pumpbare Zutaten',
      no_data_msg: 'Keine Zutaten angeschlossen!'
    },
    simple_collection: {
      headline: 'Liste: {name}',
      no_data_msg: 'Keine Rezepte gefunden!'
    },
    simple_cocktail_progress: {
      headline: 'Aktuelle Bestellung',
      producing: 'Wird hergestellt:',
      no_cocktail_in_progress: 'Aktuell wird kein Cocktail zubereitet!',
      go_back_btn_label: '<< Zurück',
      cancel_btn_label: 'Bestellung abbrechen'
    },
    recipe_details: {
      edit_btn_label: 'Bearbeiten',
      produce_btn_label: 'Cocktail zubereiten',
      delete_btn_label: 'Löschen',
      categories_headline: 'Kategorien:',
      none: 'Keine',
      default_glass_headline: 'Standardglas:',
      description_headline: 'Beschreibung:',
      notifications: {
        recipe_deleted: 'Rezept erfolgreich gelöscht'
      },
      delete_dialog: {
        headline: 'Sind Sie sicher, dass Sie dieses Rezept löschen möchten?',
        yes_btn_label: 'Löschen',
        abort_btn_label: 'Abbrechen'
      }
    },
    recipe_edit: {
      headline_new: 'Rezept hinzufügen',
      headline_edit: 'Rezept bearbeiten',
      form: {
        name: 'Name',
        categories: 'Kategorien',
        image: 'Bild',
        remove_image: 'Vorhandenes Bild entfernen',
        desc: 'Beschreibung',
        default_glass: 'Standardglas'
      },
      save_btn_label: 'Erstellen',
      update_btn_label: 'Aktualisieren',
      abort_btn_label: 'Abbrechen'
    },
    user_editor: {
      headline_edit: 'Benutzer bearbeiten',
      headline_profile: 'Mein Profil',
      headline_create: 'Benutzer erstellen',
      form: {
        columns: {
          username: 'Benutzername',
          password: 'Passwort',
          role: 'Rolle',
          locked: 'Gesperrt'
        },
        save_btn_label: 'Speichern',
        abort_btn_label: 'Abbrechen',
        edit_btn_label: 'Bearbeiten',
        roles: {
          user: 'Benutzer',
          recipe_creator: 'Rezept-Ersteller',
          pump_ingredient_editor: 'Pumpen-Zutaten-Bearbeiter',
          admin: 'Admin'
        }
      },
      notifications: {
        user_created: 'Benutzer erfolgreich erstellt',
        user_updated: 'Benutzer erfolgreich aktualisiert',
        profile_updated: 'Profil aktualisiert'
      }
    },
    i2c_mgmt: {
      headline: 'I2C-Konfiguration',
      form: {
        enable_label: 'I2C aktivieren',
        sda_pin_label: 'SDA-Pin',
        scl_pin_label: 'SCL-Pin',
        save_btn_label: 'Speichern',
        abort_btn_label: 'Abbrechen'
      },
      tutorial: 'Wenn I2C aktiviert wird, werden zwei GPIO-Pins auf dem lokalen Board für den SDA- und SCL-Pin des I2C-Busses verwendet. Einige Boards bieten mehr als einen I2C-Bus. Die Cocktailmaker-Software unterstützt jeweils nur einen I2C-Bus. Der unterstützte Bus ist das Gerät unter <div class="q-badge flex inline items-center no-wrap q-badge--single-line q-badge--outline text-black" role="status">/sys/bus/i2c/devices/i2c-1</div> im zugrundeliegenden Linux-Dateisystem. Auf normalen Raspberry Pi sind die Pins für SDL und SCL normalerweise <b>2 für SDA</b> und <b>3 für SDL</b>. Wenn Sie die Pins, die Sie verwenden möchten, hier nicht sehen können, stellen Sie sicher, dass sie nicht bereits etwas anderem zugewiesen sind.<br><br><b>Die Felder für den SDL- und SCL-Pin beeinflussen nicht den ausgewählten Bus.</b>',
      configuration_warning: '<b><u>WARNUNG!!!:</b></u> Das Aktivieren und Deaktivieren des I2C-Busses löst Unix-Befehle aus, die den I2C-Bus konfigurieren. Stellen Sie sicher, dass SDA und SCL richtig ausgewählt sind. Andernfalls kann es passieren, dass Pins gleichzeitig als normale Pins und I2C-Pins verwendet werden. Dies führt zum Absturz der Anwendung!'
    },
    pump_setup: {
      headline: 'Pumpen-Setup-Assistent',
      delete_btn_label: 'Pumpe löschen',
      name: {
        handle: 'Bezeichnung',
        headline: 'Wie soll Ihre Pumpe genannt werden?',
        pump_identifier_label: 'Pumpen-Name'
      },
      hw_pins: {
        handle: 'Hardware-Pins',
        headline: 'Wählen Sie die Pins aus, die die Pumpe steuern'
      },
      calibration: {
        handle: 'Kalibrierung',
        headline: 'Kalibrieren Sie Ihre Pumpe',
        tube_capacity_label: 'Schlauchkapazität (in ml)',
        tube_capacity_desc: 'Die Schlauchkapazität bestimmt, wie viel Flüssigkeit benötigt wird, um den Schlauch, der den Flüssigkeitsbehälter mit dem Ausgabeteil Ihrer Cocktailmaschine verbindet, zu füllen. Diese Metrik wird verwendet, um die Schläuche mit Flüssigkeit zu füllen, bevor tatsächlich ein neuer Drink hergestellt wird. Sie wird auch verwendet, um die Schläuche zu entleeren (die Flüssigkeit zurück in den Behälter zu pumpen), wenn die Maschine längere Zeit nicht benutzt wurde.',
        motor_tester: {
          headline: 'Motortester',
          disable_reason_parameter_missing: 'Erforderlicher Pumpenkonfigurationsparameter fehlt!',
          stepper_desc: 'Hier können Sie Ihren Motor testen und die Anzahl der Schritte berechnen, die der Motor benötigt, um einen cl zu pumpen. Sie können den Tester in zwei Modi ausführen: ' +
            '<ul>' +
            '<li><b>Flüssigkeit:</b> Sagen Sie dem Motor, wie viel Flüssigkeit er pumpen soll.</li>' +
            '<li><b>Schritte:</b> Sagen Sie dem Motor, wie viele Schritte er unternehmen soll.</li>' +
            '</ul>' +
            'Der Tester wird verwendet, um Ihre Konfiguration zu überprüfen und zu feinabstimmen.<br> ' +
            'Sie können den Tester auch die Anzahl der Schritte berechnen lassen, die der Motor benötigt, um einen cl zu pumpen. Hierfür müssen Sie die während Ihres Tests gepumpte Flüssigkeitsmenge (in ml) messen. Sie können dafür eine Waage verwenden. Stellen Sie außerdem sicher, dass Ihre Schläuche mit Flüssigkeit gefüllt sind, da der Tester leere Schläuche oder Luftblasen nicht berücksichtigt! Im Anschluss wird ein Dialogfeld geöffnet, in das Sie Ihre Messungen eingeben können. Der Tester wird dann die Konfiguration entsprechend Ihren Messungen korrigieren.',
          dc_desc: 'Hier können Sie Ihren Motor testen und die Zeit berechnen, die der Motor benötigt, um einen cl zu pumpen. Sie können den Tester in zwei Modi ausführen: ' +
            '<ul>\n' +
            '  <li><b>Flüssigkeit:</b> Sagen Sie dem Motor, wie viel Flüssigkeit er pumpen soll.</li>' +
            '  <li><b>Zeit:</b> Sagen Sie dem Motor, wie viele Schritte er unternehmen soll.</li>' +
            '</ul>\n' +
            'Der Tester wird verwendet, um Ihre Konfiguration zu überprüfen und zu feinabstimmen.<br>' +
            'Sie können auch den Tester die Zeit berechnen lassen, die der Motor benötigt, um einen cl zu pumpen. Hierfür müssen Sie die während Ihres Tests gepumpte Flüssigkeitsmenge (in ml) messen. Sie können dafür eine Waage verwenden. Stellen Sie außerdem sicher, dass Ihre Schläuche mit Flüssigkeit gefüllt sind, da der Tester leere Schläuche oder Luftblasen nicht berücksichtigt! Im Anschluss wird ein Dialogfeld geöffnet, in das Sie Ihre Messungen eingeben können. Der Tester wird dann die Konfiguration entsprechend Ihren Messungen korrigieren.'
        }
      },
      state: {
        handle: 'Status',
        headline: 'Pumpenstatus',
        pumped_up_label: 'Angepumpt',
        pumped_up_desc: "Das Feld 'Angepumpt' zeigt ob der Schlauch, der zu der Pumpe gehört mit Flüssigkeit gefüllt ist. Wenn die Schläuche nicht mit Flüssigkeit gefüllt sind, füllt die Maschine sie, bevor ein Cocktail hergestellt wird. Dieses Feld wird auch verwendet, um festzustellen, aus welchen Pumpen die Flüssigkeit in den Behälter zurückgepumpt werden soll, wenn die Maschine eine bestimmte Zeit nicht benutzt wurde.",
        filling_level_label: 'Aktueller Füllstand',
        filling_level_desc: 'Der aktuelle Füllstand des Behälters, der an die Pumpe angeschlossen ist. Dieses Feld wird verwendet, um zu überprüfen, ob noch genügend Flüssigkeit vorhanden ist, um einen Cocktail in einer bestimmten Größe herzustellen.',
        ingredient_label: 'Aktuelle Zutat',
        ingredient_desc: 'Optional: Die Zutat, die derzeit mit der Pumpe verbunden ist.'
      },
      caption_complete: 'Vollständig',
      caption_optional: 'Optional',
      continue_step_btn_label: 'Fortfahren',
      go_back_step_btn_label: 'Zurück',
      finish_setup_btn_label: 'Abschließen',
      delete_dialog: {
        headline: '{name} löschen?',
        yes_btn_label: 'Löschen'
      },
      notifications: {
        pump_deleted: '{name} gelöscht!'
      }
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
          desc: 'Executes a python file. The docker image uses python 3. ' +
            'You can also view the console output of your program in real-time.',
          install_lib_tip: 'Install new libraries by using: <b>pip3 install &#60;library&#62;</b>',
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
        'The developer of CocktailMaker (me) develops it in his free time. I\'m also a student who doesn\'t have a ' +
        'real income yet.',
      main_text: '<p>Here are some more reasons why you should donate:</p>' +
        '            <ul>\n' +
        '              <li>\n' +
        '                Users of this software usually spent large amounts of money on hardware (a Raspberry Pi, Pumps, a case,\n' +
        '                ...).\n' +
        '                Nevertheless this hardware wouldn\'t function if the CocktailMaker software wouldn\'t exist.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                You might think that that someone else will donate, but sadly they think that too. Before adding this\n' +
        '                disclaimer I got around 50€ of donations in two years\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                I usually don\'t get any feedback for the software. It doesn\'t collect any data. I have no idea how many\n' +
        '                people are out there using it.\n' +
        '                A donation and also "stars" on GitHub give me positive feedback and motivate me to continue working on\n' +
        '                CocktailMaker.\n' +
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
      cocktail_prod_finished: 'Cocktail production finished',
      cocktail_prod_canceled: 'Cocktail production canceled',
      application_started: 'Application started'
    }
  }
}
