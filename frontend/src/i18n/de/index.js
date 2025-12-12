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
    machine_name: 'CocktailPi',
    profile: {
      profile_btn_label: 'Profil',
      reload_btn_label: 'Neu laden',
      logout_btn_label: 'Abmelden'
    }
  },
  simple_header: {
    machine_name: 'CocktailPi',
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
    collection: {
      modify_recipes: 'Rezepte bearbeiten',
      stop_modify_recipes: 'Rezepte bearbeiten beenden',
      delete: 'Liste löschen',
      add_recipe: 'Rezept hinzufügen',
      delete_dialog: {
        ok_btn_label: 'Löschen',
        headline: 'Liste \'{name}\' löschen?'
      },
      form: {
        name: 'Name',
        image: 'Bild',
        remove_img: 'Vorhandenes Bild entfernen',
        desc: 'Beschreibung',
        edit_btn_label: 'Bearbeiten',
        save_btn_label: 'Speichern',
        abort_btn_label: 'Abbrechen'
      },
      notifications: {
        recipe_added: 'Rezept erfolgreich hinzugefügt',
        recipe_removed: 'Rezept erfolgreich entfernt',
        collection_updated: 'Liste erfolgreich aktualisiert'
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
      no_ingredients_available: 'Keine Zutaten angeschlossen!',
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
          text: "Zutatengruppen sind abstrakte Gruppen, die aus mehreren Zutaten oder anderen Zutatengruppen bestehen können. Rezepte können Zutatengruppen enthalten. Bevor ein solches Rezept bestellt werden kann, muss der Benutzer diese Zutatengruppe durch eine konkrete Zutat ersetzen, die zu dieser Gruppe gehört. Die Software erledigt dies automatisch für den Benutzer, wenn eine solche Zutat einer Pumpe zugeordnet wurde oder als 'Bar'-Zutat markiert ist. Zutaten auf Pumpen haben Vorrang.\n\nBeispiel: Sie haben eine spezifische Zutat namens 'Whisky 08/15'. Dieser Whisky ist in der Zutatengruppe 'Bourbon' kategorisiert. Die Gruppe 'Bourbon' wiederum fällt unter die allgemeinere Kategorie 'Whisky'. Es gibt eine zusätzliche Kindzutatengruppe, die mit 'Whisky' verbunden ist, bekannt als 'Scotch'.\n\nSie haben 'Whisky 08/15' in Ihrem Bar-Inventar. Aufgrund der hierarchischen Struktur erkennt CocktailPi nun, dass 'Whisky 08/15' nicht nur in Rezepten verwendet werden kann, die diese spezielle Zutat erfordern, sondern auch in verschiedenen Rezepten, die nach einen beliebigen 'Bourbon' oder 'Whisky' verlangen. Es ist jedoch wichtig zu beachten, dass 'Whisky 08/15' nicht als Ersatz für Rezepte dienen kann, die explizit 'Scotch' oder andere spezifische Zutaten beinhalten."
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
          empty_weight: 'Leergewicht',
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
        start_btn_tooltip: 'Start',
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
          reboot_btn_label: 'Neu starten',
          shutdown_btn_label: 'Herunterfahren',
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
      },
      appearance: {
        headline: 'Darstellung'
      }
    },
    load_cell_mgmt: {
      headline: 'Wägezelle',
      hardware_settings: {
        headline: 'Hardwareeinstellungen',
        enable_btn_label: 'Wägezelle aktivieren',
        clk_pin_label: 'CLK-Pin',
        dt_pin_label: 'DT-Pin',
        cocktail_production_label: 'Cocktailproduktion',
        glass_detection_label: 'Prüfe ob sich ein Glas im Ausgabebereich befindet',
        glass_matching_label: 'Gemessenes Gewicht nutzen um Glastyp zu identifizieren',
        save_btn_label: 'Speichern',
        save_and_return_btn_label: 'Speichern & Zurück',
        update_success_message: 'Wägezelle gespeichert'
      },
      calibration: {
        headline: 'Kalibrierung',
        calibrated_batch_yes: 'Ok',
        calibrated_batch_no: 'Fehlt',
        next_btn_label: 'Weiter',
        zero_point: {
          headline: 'Nullpunkt-Kalibrierung (Ohne Gewicht)',
          text: 'Stellen Sie sicher, dass der Ausgabebereich leer ist. Drücken Sie die Weiter-Taste, um die Nullpunktmessung der Wägezelle zu erfassen.'
        },
        known_weight: {
          headline: 'Kalibrierung mit bekanntem Gewicht',
          text: 'Platzieren Sie ein bekanntes Gewicht im Ausgabebereich. Geben Sie den Wert des Gewichts in Gramm in das Eingabefeld ein. Drücken Sie die Weiter-Taste, um die Wägezelle auf das bekannte Gewicht kalibrieren.',
          ref_weight_field_label: 'Referenzgewicht (in g)'
        },
        validation: {
          headline: 'Validierungstest',
          text: 'Platzieren Sie ein beliebiges Gewicht im Abgabebereich. Drücken Sie die Messen-Taste, um die Wägezelle auszulesen und ausgeben zu lassen.',
          measure_btn_label: 'Messen',
          measure_field_label: 'Messung (in g)',
          finish_and_return_btn_label: 'Beenden & Zurück',
          start_over_btn_label: 'Neustart'
        }
      }
    },
    power_limit_mgmt: {
      headline: 'Leistungbegrenzung',
      hardware_settings: {
        headline: 'Leistungbegrenzung Einstellungen',
        enable_btn_label: 'Leistungbegrenzung aktivieren',
        power_limit_label: 'Maximale Leistung (in mW)',
        save_btn_label: 'Speichern',
        save_and_return_btn_label: 'Speichern & Zurück',
        update_success_message: 'Strombegrenzung gespeichert'
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
      no_glass_label: 'Kein Glas',
      glass_label: '({ml}ml) {glass}',
      no_alc_label: 'Alkoholfrei',
      alc_label: '{val}% vol.',
      alc_label_range: '{min_val}-{max_val}% vol.',
      description_headline: 'Beschreibung',
      ingredients_headline: 'Zutaten',
      category_headline: 'Kategorien',
      property_last_change: 'Letzte Änderung:',
      property_recipe_owner: 'Rezeptinhaber:',
      property_id: 'ID:',
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
          admin: 'Admin',
          super_admin: 'Super-Admin'
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
      tutorial: 'Wenn I2C aktiviert wird, werden zwei GPIO-Pins auf dem lokalen Board für den SDA- und SCL-Pin des I2C-Busses verwendet. Einige Boards bieten mehr als einen I2C-Bus. Die CocktailPi-Software unterstützt jeweils nur einen I2C-Bus. Der unterstützte Bus ist das Gerät unter <div class="q-badge flex inline items-center no-wrap q-badge--single-line q-badge--outline text-black" role="status">/sys/bus/i2c/devices/i2c-1</div> im zugrundeliegenden Linux-Dateisystem. Auf normalen Raspberry Pi sind die Pins für SDA und SCL normalerweise <b>2 für SDA</b> und <b>3 für SCL</b>. Wenn Sie die Pins, die Sie verwenden möchten, hier nicht sehen können, stellen Sie sicher, dass sie nicht bereits etwas anderem zugewiesen sind.<br><br><b>Die Felder für den SDA- und SCL-Pin beeinflussen nicht den ausgewählten Bus.</b>',
      configuration_warning: '<b><u>WARNUNG!!!:</b></u> Das Aktivieren und Deaktivieren des I2C-Busses löst Unix-Befehle aus, die den I2C-Bus konfigurieren. Stellen Sie sicher, dass SDA und SCL richtig ausgewählt sind. Andernfalls kann es passieren, dass Pins gleichzeitig als normale Pins und I2C-Pins verwendet werden. Dies führt zum Absturz der Anwendung!'
    },
    pump_setup: {
      headline: 'Pumpen-Setup-Assistent ({pumpName})',
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
        power_consumption_label: 'Stromverbrauch (in mW)',
        power_consumption_desc: 'Wie viel Leistung benötigt diese Pumpe? CocktailPi lässt, nur so viele Pumpen gleichzeitig laufen, wie das Netzteil gleichzeitig versorgen kann. Dieser Parameter has nur dann einen Effekt, wenn das \'Leistungsbegrenzungs\'-Feature aktiviert wurde!',
        power_consumption_link_text: 'Leistungsbegrenzung einstellen',
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
    },
    transfer: {
      headline: 'Transfer',
      export_btn_label: 'Exportieren',
      import_btn_label: 'Importieren',
      import: {
        stepper: {
          select_file: {
            label: 'Datei auswählen',
            select_file_btn_label: 'Importdatei auswählen (.zip)',
            upload_btn_label: 'Hochladen'
          },
          import_selection: {
            label: 'Importauswahl',
            recipes: {
              headline: 'Rezepte',
              selection_all: 'Alle importieren',
              selection_selected: 'Auswahl importieren',
              selection_none: 'Keine importieren'
            },
            collections: {
              headline: 'Listen',
              selection_all: 'Alle importieren',
              selection_selected: 'Auswahl importieren',
              selection_none: 'Keine importieren'
            },
            glasses: {
              headline: 'Gläser',
              selection_all: 'Alle importieren',
              selection_none: 'Keine importieren'
            },
            categories: {
              headline: 'Kategorien',
              selection_all: 'Alle importieren',
              selection_none: 'Keine importieren'
            },
            duplicates: {
              headline: 'Strategie bei Duplikaten',
              overwrite: 'Überschreiben',
              skip: 'Überspringen',
              keep: 'Beide behalten'
            },
            start_import_btn_label: 'Import starten',
            error_upload_file_first: 'Bitte zuerst eine Importdatei hochladen.'
          },
          complete: {
            label: 'Fertig',
            successful: 'Import erfolgreich'
          }
        }
      },
      export: {
        recipes: {
          export_all: 'Alle Rezepte exportieren',
          export_selection: 'Ausgewählte Rezepte exportieren',
          export_none: 'Keine Rezepte exportieren'
        },
        collections: {
          export_all: 'Alle Listen exportieren',
          export_selection: 'Ausgewählte Listen exportieren',
          export_none: 'Keine Listen exportieren'
        },
        download_btn_label: 'Export herunterladen'
      }
    }
  },
  component: {
    show_img_dialog: {
      close_btn_label: 'Schließen'
    },
    simple_recipes_filter_drawer: {
      headline: 'Filter',
      open_btn_label: 'Filter',
      name_field_label: 'Name',
      contains_ingredients_field_label: 'Enthält Zutaten',
      order_by_selector_label: 'Sortieren nach',
      fabricable_box: {
        headline: 'Herstellbar:',
        show_all: 'Alle anzeigen',
        show_all_desc: 'Zeigt alle Rezepte an',
        fabricable_owned: 'Herstellbar mit vorhandenen Zutaten',
        fabricable_owned_desc: 'Zeigt nur Rezepte an, die mit vorhandenen Zutaten hergestellt werden können',
        fabricable_auto: 'Vollautomatisch herstellbar',
        fabricable_auto_desc: 'Zeigt nur Rezepte an, die vollautomatisch hergestellt werden können'
      },
      search_btn_label: 'Suchen',
      reset_btn_label: 'Zurücksetzen'
    },
    simple_recipe_card: {
      no_alc: 'Kein Alc.',
      percent_alc: '{percent}% Alc.',
      percent_range_alc: '{min} - {max}% Alc.'
    },
    simple_collection_card: {
      name: '{name}',
      nr_cocktails: '{nr} Cocktail(s)',
      desc: '{desc}',
      owner: 'von {owner}'
    },
    recipe_search_list: {
      loading: 'Laden...'
    },
    recipe_list: {
      no_recipes_found: 'Keine Rezepte gefunden!'
    },
    recipe_search_filter_card: {
      headline: 'Suchoptionen',
      expert_search_label: 'Experten-Suche',
      cocktail_name_field_label: 'Suchen',
      fabricable_box: {
        headline: 'Herstellbar:',
        show_all: 'Alle anzeigen',
        fabricable_owned: 'Herstellbar mit vorhandenen Zutaten',
        fabricable_auto: 'Vollautomatisch herstellbar'
      },
      contains_ingredient_field_label: 'Enthält Zutaten',
      order_by_selector_label: 'Sortieren nach',
      reset_btn_label: 'Filter zurücksetzen'
    },
    recipe_card: {
      owner_name: 'von {name}',
      ingredient_add_manually_tooltip: 'Manuell hinzufügen',
      ingredient_not_owned_tooltip: 'Nicht im Besitz',
      no_alc: 'Kein Alk.',
      percent_alc: '{percent}% Alk.',
      percent_range_alc: '{min} - {max}% Alk.'
    },
    ingredient_list: {
      headline: 'Produktionsschritte',
      no_steps_added: 'Keine Produktionsschritte hinzugefügt!',
      alc_content: '{nr}% Alkoholgehalt',
      alc_content_range: '{min} - {max}% Alkoholgehalt',
      manual_instruction: 'Manueller Schritt:',
      tag_boostable: 'Boostbar',
      tag_unscaled: 'Unskaliert',
      edit_dialog: {
        edit_headline: 'Produktionsschritt bearbeiten',
        add_headline: 'Produktionsschritt hinzufügen',
        save_btn_label: 'Speichern',
        abort_btn_label: 'Abbrechen'
      }
    },
    prod_step_editor: {
      tab_ingredient: 'Zutat',
      tab_instruction: 'Anleitung'
    },
    prod_step_editor_ingredient: {
      amount: 'Menge',
      amount_in: 'Menge (in {metric})',
      scale_label: 'Mit Volumen skalieren',
      boostable_label: 'Boostbar'
    },
    prod_step_editor_instruction: {
      instruction_label: 'Anweisung'
    },
    ingredient_selector: {
      default_label: 'Zutat',
      alc_content: '{nr}% Alkoholgehalt',
      alc_content_range: '{min} - {max}% Alkoholgehalt'
    },
    make_cocktail_dialog: {
      headline: 'Cocktail bestellen',
      order_btn_label: 'COCKTAIL HERSTELLEN ({nr} ml)'
    },
    make_cocktail_amount_to_produce: {
      glass_selector_label: 'Glas',
      badge_auto_detected: 'Automatisch erkannt',
      badge_recipe_default: 'Rezeptstandard',
      badge_global_default: 'Globaler Standard',
      amount_to_produce_label: 'Zu produzierende Menge'
    },
    make_cocktail_group_replacements: {
      fulfilled_msg: 'Alle Zutaten-Gruppen wurden durch konkrete Zutaten ersetzt!',
      not_fulfilled_msg: 'Die folgenden Zutaten-Gruppen müssen durch konkrete Zutaten ersetzt werden:',
      card: {
        prod_step_label: 'Produktionsschritt',
        ingredient_group_label: 'Zutatengruppe',
        replacement_label: 'Ersatz',
        tags: {
          in_bar: 'in Bar',
          not_in_bar: 'nicht in Bar',
          automatically_addable: 'Automatisch hinzufügbar'
        }
      }
    },
    pump_status: {
      headline: 'Status',
      configure_btn: 'Konfigurieren',
      pumps: {
        headline: 'Pumpen',
        pumps_installed: 'Pumpen erstellt:',
        ingredients_installed: 'Zutaten zugewiesen:'
      },
      reverse_pumping: {
        headline: 'Rückpumpen',
        status: 'Status:',
        status_enabled: 'Aktiviert',
        status_disabled: 'Deaktiviert',
        overshoot: 'Overshoot:',
        timer: 'Timer:',
        timer_no_timer_label: 'Niemals',
        overshoot_no_overshoot_label: 'Nein'
      },
      load_cell: {
        headline: 'Wägezelle',
        status: 'Status:',
        status_enabled: 'Aktiviert',
        status_disabled: 'Deaktiviert',
        calibrated: 'Kalibriert',
        calibrated_yes: 'Ja',
        calibrated_no: 'Nein',
        check_glass_placed: 'Glas erkennen:',
        check_glass_placed_yes: 'Ja',
        check_glass_placed_no: 'Nein',
        match_glass: 'Glas abgleichen:',
        match_glass_yes: 'Ja',
        match_glass_no: 'Nein'
      },
      power_limit: {
        headline: 'Leistungbegrenzung',
        status: 'Status:',
        status_enabled: 'Aktiviert',
        status_disabled: 'Deaktiviert',
        limit_label: 'Limit:',
        limit_value: '{limit} mW'
      }
    },
    make_cocktail_occupied: {
      fulfilled_msg: 'Maschine ist nicht belegt!',
      occupied_cocktail_msg: 'Maschine belegt! Ein Cocktail wird gerade zubereitet!',
      occupied_pumps_msg: 'Maschine belegt! Eine oder mehrere Pumpen werden gerade gereinigt/angepumpt!'
    },
    make_cocktail_insufficient_ingredients: {
      fulfilled_msg: 'Die folgenden Zutaten werden verbraucht:',
      insufficient_filling_level: 'Cocktail kann nicht hergestellt werden! Einige Pumpen-Behälter haben nicht mehr genügend Flüssigkeit:',
      unassigned_ingredients: 'Einige Zutaten müssen manuell hinzugefügt werden. Sie werden während des Produktionsprozesses dazu aufgefordert:',
      tags: {
        in_bar: 'in Bar / manuell hinzufügen',
        not_in_bar: 'not in Bar / manuell hinzufügen'
      }
    },
    make_cocktail_no_glass: {
      not_fulfilled_msg: 'Kein Glas im Ausgabebereich platziert.'
    },
    make_cocktail_customizer: {
      headline: 'Bestellanpassung',
      headline_boosting: 'Boosting:',
      boosting_desc: 'Erhöht (oder verringert) die Menge der im Rezept als boostbar ausgewiesenen Zutaten. (Normalerweise Spirituosen) Nicht boostbare Zutaten werden hernunter- oder hochskaliert. Die Menge der abgegebenen Flüssigkeit bleibt gleich!',
      headline_additional_ingredients: 'Zusätzliche Zutaten:',
      additional_ingredients_desc: 'Diese Zutaten werden als weiterer Produktionsschritt hinzugefügt. Die abgegebene Flüssigkeitsmenge wird um die Menge der bestellten zusätzlichen Zutaten erhöht.',
      add_new_ingredient_btn_label: 'Neue Zutat hinzufügen',
      add_new_ingredient_headline: 'Zutat hinzufügen',
      recipe_not_boostable: 'Rezept nicht boostbar!'
    },
    make_cocktail_pump_editor: {
      headline: 'Pumpenlayout',
      pump_table: {
        required_ingredient: 'Erforderliche Zutat',
        columns: {
          nr: 'Nr',
          ingredient: 'Aktuelle Zutat',
          filling_level: 'Verbleibender Füllstand',
          pumped_up: 'Angepumpt',
          state: 'Status',
          actions: 'Aktionen'
        },
        refill_btn_label: 'Auffüllen',
        state_ok: 'OK',
        state_incomplete: 'Unvollständig'
      }
    },
    pump_up_btn: {
      tooltip_up: 'Pumpen',
      tooltip_back: 'Rückpumpen'
    },
    pump_turn_on_btn: {
      tooltip_on: 'Einschalten',
      tooltip_off: 'Ausschalten'
    },
    deleteWarning: {
      delete_btn_label: 'Löschen',
      abort_btn_label: 'Abbrechen',
      success_notification: 'Erfolgreich gelöscht!',
      nothing_selected: 'Nichts ausgewählt!'
    },
    editDialog: {
      save_btn_label: 'Speichern',
      abort_btn_label: 'Abbrechen'
    },
    ingredientGroupForm: {
      name: 'Name',
      parent_group: 'Übergeordnete Gruppe'
    },
    ingredient_group_mgmt: {
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
        delete_btn_tooltip: 'Löschen',
        delete_dialog: {
          headline: 'Gruppe löschen?',
          warning: 'Die ausgewählte Gruppe wird auch aus allen zugehörigen Rezepten entfernt!'
        }
      },
      edit_headline: 'Gruppe bearbeiten',
      create_headline: 'Gruppe erstellen',
      notifications: {
        group_created: 'Gruppe erfolgreich erstellt',
        group_updated: 'Gruppe erfolgreich aktualisiert'
      }
    },
    ingredient_mgmt: {
      create_btn_label: 'Zutat erstellen',
      refresh_btn_label: 'Aktualisieren',
      ingredient_table: {
        search_field_label: 'Suche',
        columns: {
          ingredient: 'Zutat',
          type: 'Typ',
          alc_content: 'Alkoholgehalt',
          bottle_size: 'Behältergröße',
          unit: 'Einheit',
          pump_time_multiplier: 'Pumpenzeitmultiplikator',
          parent_group: 'Übergeordnete Gruppe',
          has_image: 'Bild',
          actions: 'Aktionen'
        },
        has_img_col: {
          no_label: 'Nein',
          yes_btn_label: 'Ja / Anzeigen'
        },
        edit_btn_tooltip: 'Bearbeiten',
        delete_btn_tooltip: 'Löschen'
      },
      edit_dialog: {
        headline_create: 'Zutat erstellen',
        headline_edit: 'Zutat bearbeiten'
      },
      delete_dialog: {
        headline: 'Zutat löschen?',
        warning: 'Die ausgewählte Zutat wird auch aus allen zugehörigen Rezepten entfernt!'
      },
      notifications: {
        ingredient_created: 'Zutat erfolgreich erstellt',
        ingredient_updated: 'Zutat erfolgreich aktualisiert'
      }
    },
    ingredient_form: {
      name: 'Name',
      alc_content: 'Alkoholgehalt',
      tab_automated: 'Automatisch',
      tab_manual: 'Manuell',
      parent_group: 'Übergeordnete Gruppe',
      bottle_size: 'Flaschengröße',
      image: 'Bild',
      remove_img: 'Vorhandenes Bild entfernen',
      pump_time_multiplier: 'Pumpenzeitmultiplikator',
      unit: 'Einheit',
      units: {
        gram: 'Gramm (g)',
        milliliter: 'Milliliter (ml)',
        piece: 'Stück(e)',
        teaspoon: 'Teelöffel',
        tablespoon: 'Esslöffel'
      }
    },
    gpio_expander_expansion_item: {
      caption_local: 'Board: Lokal, Nutzung: {pinsUsed}/{pinsMax}',
      caption_i2c: 'Adresse: {addr}, Board: {board}, Nutzung: {pinsUsed}/{pinsMax}',
      i2c_backend_restarted: 'I2C backend neu gestarted'
    },
    pump_mgmt: {
      headline: 'Pumpenverwaltung',
      add_btn_label: 'Hinzufügen',
      start_all_btn_label: 'Alle starten',
      stop_all_btn_label: 'Alle stoppen',
      show_pump_details_btn_label: 'Details anzeigen',
      hide_pump_details_btn_label: 'Details ausblenden',
      no_pumps_found: 'Keine Pumpen gefunden!',
      notifications: {
        all_stopped: 'Alle Pumpen gestoppt!',
        all_started: 'Alle Pumpen gestartet!'
      }
    },
    pump_setup_type_selector: {
      headline: 'Welchen Pumpentyp möchten Sie hinzufügen?',
      dc_pump: 'Gleichstrompumpe',
      valve: 'Ventil',
      stepper_pump: 'Schrittmotorpumpe'
    },
    pump_card: {
      dc_pump: 'Gleichstrompumpe',
      stepper_pump: 'Schrittmotorpumpe',
      valve: 'Valve',
      option_missing: '-- fehlt --',
      load_cell_not_calibrated: '-- nicht kalibriert --',
      no_ingredient_placeholder: 'Keine',
      fallack_name: 'Pumpe #{id}',
      attr: {
        ingredient: 'Zutat',
        filling_level: 'Füllstand',
        time_per_cl: 'Zeit pro Cl',
        enable_pin: 'Aktivierungs-Pin',
        running_state: 'GPIO-Laufzustand',
        tube_capacity: 'Schlauchkapazität',
        steps_per_cl: 'Schritte pro Cl',
        acceleration: 'Beschleunigung',
        max_steps_per_second: 'Maximale Schritte pro Sekunde',
        step_pin: 'Schrittpin',
        load_cell: 'Wägezelle',
        power_limit: 'Leistung',
        power_limit_text: '{limit} mW'
      },
      pumpStates: {
        ready: 'Bereit',
        incomplete: 'Unvollständig',
        running: 'Läuft...',
        suspended: 'Ausgesetzt'
      },
      pumpUpStates: {
        pumped_up: 'Angepumpt',
        pumped_down: 'Abgepumpt'
      },
      notifications: {
        pump_started: '{name} gestartet!',
        pump_stopped: '{name} gestoppt!'
      }
    },
    reverse_pump_settings: {
      headline: 'Rückpumpen',
      form: {
        timer_options: {
          in_minutes: '{nr} Minuten',
          never: 'Nie'
        },
        enable_label: 'Rückpumpen aktivieren',
        vd_pin_headline: 'Spannungsinverter-Pin',
        vd_pin_label: 'Spannungsinverter-Pin',
        overshoot_label: 'Überschuss',
        overshoot_hint: 'Wie stark sollte die Anzahl ml beim Zurückpumpen überschritten werden?',
        auto_pump_back_timer_label: 'Inaktive Zeit bis zum automatischen Zurückpumpen',
        save_btn_label: 'Speichern & Zurück'
      },
      notifications: {
        updated: 'Einstellungen aktualisiert!'
      }
    },
    pump_tester: {
      unknown_job_running: 'Unbekannter Job läuft!',
      default_disable_reason: 'Deaktiviert!',
      ref_metric: 'Metrik:',
      metrics: {
        liquid: 'Flüssigkeit',
        liquid_pumped: 'Gepumpte Flüssigkeit:',
        liquid_run_val_field: 'Ml zum Pumpen',
        steps: 'Schritte',
        steps_made: 'Gemachte Schritte:',
        steps_run_val_field: 'Zu laufende Schritte',
        time: 'Zeit',
        time_taken: 'Verstrichene Zeit:',
        time_run_val_field: 'Ms zum Laufen',
        unknown_run_metric: 'Unbekannte Metrik'
      },
      true_liquid_pumped_field: 'Tatsächlich gepumpte ml',
      apply_per_cl_metric_value_btn_label: 'Anwenden',
      run_btn_label: 'Start',
      stop_btn_label: 'Stop'
    },
    pump_setup_dc_hw_pins: {
      control_pin_label: 'Steuerungs-Pin',
      control_pin_desc: "Ein Gleichstrommotor kann ein- und ausgeschaltet werden, indem er mit einer Stromquelle verbunden und von dieser getrennt wird. Dies geschieht in der Regel mit Hilfe eines Relais. Das Relais öffnet und schließt den elektronischen Stromkreis, mit dem der Motor verbunden ist. Das Feld 'Steuerungs-Pin' konfiguriert die Pin, mit der das Relais gesteuert wird.<br><br><b>Wichtig:</b> Für die lokelen Pins, welche sich direkt auf ihrem Raspberry Pi befinden, entspricht die Nummer die hier ausgewählt werden muss nicht der GPIO-Nummer. Für das lokale Board, müssen statt den GPIO-Nummern sogenannte BCM-Nummern verwendet werden. BCM steht für 'Broadcom SOC channel', und bezeichnet die Nummer der Pin innerhalb des Chips, der vom Raspberry Pi verwendet wird. Diese Nummern haben sich eventuell zwischen den Board-Versionen geändert. Dieser Link kann helfen:",
      power_state_desc: 'Je nach Hardware-Aufbau läuft der Motor entweder wenn die Pin, der die Pumpe steuert, auf HIGH oder LOW gesetzt wird. Bitte wählen Sie den Zustand, in welchem Ihre Pumpe in Ihrer Konfiguration eingeschaltet wäre.',
      power_state_label: 'Pinzustand',
      power_state_options: {
        high: 'HIGH',
        low: 'LOW'
      }
    },
    pump_setup_dc_calibration: {
      time_per_cl_pin_label: 'Zeit pro cl in ms',
      time_per_cl_pin_desc: '"Zeit pro cl in ms" bestimmt, wie viele Millisekunden (ms) die Pumpe benötigt, um einen Zentiliter (cl) zu pumpen. Dieser Wert wird verwendet, um festzustellen, wie lange die Pumpe laufen muss, um die gewünschte Menge Flüssigkeit aus der Flasche zu pumpen.'
    },
    pump_setup_stepper_hw_pins: {
      step_pin_label: '(lokal) Schritt-Pin',
      enable_pin_label: 'Aktivierungs-Pin',
      pin_desc: 'Ein Schrittmotortreiber hat normalerweise drei wichtige Pins, die zur Steuerung des Motors verwendet werden.' +
        '<ul>' +
        '  <li>' +
        '    Der Schrittpin (Step), der für jeden Schritt, den der Motor machen soll, einen Impuls empfängt.' +
        '  </li>' +
        '  <li>' +
        '    Der Aktivierungspin (Enable). Dieser Pin entscheidet, ob der Motor aktiviert sein sollte und somit seine aktuelle Position halten soll oder nicht.' +
        '  </li>' +
        '  <li>' +
        '    Der Richtungspin (Dir). Er entscheidet über die Richtung, die der Motor nimmt. Die Richtung, in die der Motor läuft, wird von einem einzelnen Pin bestimmt, der die Richtung für alle Motoren steuert. Dieser Pin wird in der Pumpenübersicht definiert und ist kein Teil dieses Setups! Bitte konfigurieren Sie Ihre Maschine so, dass diese Pin mit der Richtungslogik aller Ihrer Motoren verbunden ist.' +
        '  </li>' +
        '  <li>' +
        '    Ihr Motortreiber bietet höchstwahrscheinlich auch weitere Pins (Schrittauflösung/Schlaf/...). Bitte konfigurieren Sie diese statisch in der Hardware!' +
        '  </li>' +
        '</ul>' +
        "<b>Wichtig:</b> Für die lokelen Pins, welche sich direkt auf ihrem Raspberry Pi befinden, entspricht die Nummer die hier ausgewählt werden muss nicht der GPIO-Nummer. Für das lokale Board, müssen statt den GPIO-Nummern sogenannte BCM-Nummern verwendet werden. BCM steht für 'Broadcom SOC channel', und bezeichnet die Nummer der Pin innerhalb des Chips, der vom Raspberry Pi verwendet wird. Diese Nummern haben sich eventuell zwischen den Board-Versionen geändert. Dieser Link kann helfen:"
    },
    pump_setup_stepper_calibration: {
      acceleration_label: 'Beschleunigung',
      acceleration_desc: 'Das Beschleunigungsfeld bestimmt, wie schnell der Motor beschleunigen oder bremsen soll. Ist die Beschleunigung zu hoch, kann der Motor Schritte überspringen, wenn er beschleunigt, oder zu viele Schritte machen, wenn er bremst. Die Beschleunigung wird in Schritten pro Sekunde zum Quadrat angegeben.',
      max_steps_per_second_label: 'Maximale Anzahl an Schritten pro Sekunde',
      max_steps_per_second_desc: '<p>' +
        "        Das Feld 'Maximale Anzahl an Schritten pro Sekunde' bestimmt, wie schnell der Motor maximal rotieren soll. " +
        '        Eine Umdrehung wird normalerweise in 200 Schritte unterteilt. Dies kann jedoch je nach Motor und Motortreiber-Einstellungen variieren. ' +
        '        Ist der Wert zu hoch, kann der Motor nicht mithalten und überspricht eventuell Schritte oder läuft sogar gar nicht. ' +
        '        Ist der Wert zu niedrig, läuft der Motor langsamer als notwendig.<br>' +
        '      </p>' +
        '      <p>' +
        '        Die Regel lautet:' +
        '      </p>' +
        '      <ul>' +
        '        <li>höher = schnellerer Motor</li>' +
        '        <li>niedriger = langsamerer Motor</li>' +
        '      </ul>',
      steps_per_cl_label: 'Schritte pro cl',
      steps_per_cl_desc: 'Dieses Feld bestimmt, wie viele Schritte der Motor machen muss, um einen cl zu pumpen.'
    },
    glass_form: {
      name: 'Name',
      size: 'Größe (in ml)',
      empty_weight: 'Leergewicht (in g)',
      default_checkbox: 'Standard',
      use_for_single_ingredients_checkbox: 'Verwenden für einzelne Zutaten',
      load_cell_measure_btn_label: 'Messen'
    },
    event_action_editor_form: {
      comment_label: 'Kommentar',
      execution_groups: {
        label: 'Ausführungsgruppen',
        write_to_create: 'Schreiben, um eine neue Gruppe zu erstellen...',
        create_new: '<b>Neu:</b> {group}'
      },
      trigger_label: 'Auslöser',
      action: {
        label: 'Aktion',
        select_action: 'Wählen Sie eine Aktion aus, um Konfiguration anzuzeigen...',
        options: {
          call_url: 'URL aufrufen',
          audio: 'Audiodatei abspielen',
          python: 'Python-Skript ausführen',
          nothing: 'Nichts tun'
        },
        call_url: {
          request_method_label: 'Methode',
          url_label: 'URL'
        },
        audio: {
          file_label: 'Audio (max. 20 MB)',
          volume: 'Lautstärke {nr}%',
          output_device_label: 'Ausgabegerät',
          on_repeat_label: 'Dauerschleife'
        },
        python: {
          desc: 'Führt eine Python-Datei aus. Es wird Python 3 verwendet. ' +
            'Die Konsolenausgabe Ihres Programms wird in Echtzeit anzeigt.',
          install_lib_tip: 'Neue Bibliotheken können mit <b>&#60;Stammordner&#62;/venv/bin/pip3 install &#60;Bibliothek&#62;</b> installiert werden, wobei "Stammordner" dem Ordner entspricht, in welchem die cocktailpi-jar-datei liegt. Bitte beachte, dass der venv-Ordner erst stellet wird, nachdem die python Libraries mit dem Button unterhalb ausgelesen wurden, oder ein Pythonskript über das Eventsystem ausgeführt wurde.',
          fetching_libs: 'Bibliotheken laden',
          lib_list_lib: 'Bibliothek',
          lib_list_version: 'Version',
          no_libs_found: 'Keine Bibliotheken installiert',
          python_file_label: 'Python (max. 20 MB)'
        },
        nothing: {
          desc: 'Diese Aktion tut nichts. Aber bricht andere andere laufende Aktionen ab, die keine Ausführungsgruppen mit ihr teilen.'
        }
      }
    },
    circular_cocktail_progress: {
      headline: 'Derzeit hergestellter Cocktail',
      no_cocktail_msg: 'Derzeit wird kein Cocktail hergestellt!<br>' +
        "Gehen Sie zu 'Meine Rezepte' oder 'Cocktails', um einen Cocktail zu bestellen.",
      close_btn_label: 'Schließen'
    },
    cocktail_production_manual_step_card: {
      continue_btn_label: 'Fortsetzen',
      add_ingredient_headline: "Bitte fügen Sie die folgenden Zutaten manuell hinzu und klicken Sie auf 'Fortsetzen':"
    },
    donation_disclaimer: {
      headline: 'Deine Unterstützung wird benötigt!',
      headline_caption: 'Zum schließen nach unten scrollen',
      donate_paypal_btn_label: 'Spenden via PayPal',
      donate_github_btn_label: 'Spenden via GitHub Sponsors',
      headline_2: 'Diese Software ist kostenlos, aber Spenden werden gerne gesehen!',
      introduction: 'Diese Software ist kostenlos, aber ihre Entwicklung und Wartung erfordern viel Zeit. Der Entwickler von CocktailPi (ich) entwickelt sie in seiner Freizeit.',
      main_text: '<p>Hier sind einige weitere Gründe, warum du spenden solltest:</p>' +
        '            <ul>\n' +
        '              <li>\n' +
        '                Nutzer dieser Software geben normalerweise viel Geld für Hardware aus (einen Raspberry Pi, Pumpen, ein Gehäuse,\n' +
        '                ...).\n' +
        '                Diese Hardware wäre jedoch nutzlos, wenn die CocktailPi-Software nicht existieren würde. Eine Spende zeigt deine Wertschätzung meiner Arbeit und sollte im Vergleich zu den Hardwarekosten nicht mehr sonderlich stark ins Gewicht fallen.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                Du könntest denken, dass schon jemand anderes spenden wird. Diese ist jedoch leider meistens nicht der Fall. Die anderen denken sich das nämlich auch. Bevor ich diesen\n' +
        '                Disclaimer hinzugefügt habe, habe ich in zwei Jahren rund 50€ an Spenden erhalten.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                Normalerweise erhalte ich kein Feedback für die Software. Sie sammelt keine Daten. Ich habe keine Ahnung, wie viele\n' +
        "                Menschen sie verwenden. Eine Spende und auch 'Stars' auf GitHub geben mir positives Feedback und motivieren mich, weiter an\n" +
        '                der CocktailPi-Software zu arbeiten.\n' +
        '              </li>\n' +
        '              <li>\n' +
        '                Die Entwicklung dieser Software verursacht Kosten. Ich als Entwickler kaufe oft Hardware, nur um zu testen,\n' +
        '                ob sie mit dem Gerät funktionieren und sinnvoll ist. Hierdurch mache ich mit der Entwicklung der Software sogar verlust, da ich die Hardware oft selbst garnicht verwende.\n' +
        '              </li>\n' +
        '            </ul>' +
        '            <p>\n' +
        '              Spenden sind freiwillig. Während die Software weiterhin kostenlos bleibt, zeigt dein Beitrag deine Wertschätzung\n' +
        '              für die Arbeit und aufgewendete Zeit, die für die Entwicklung dieser Software nötig waren.\n' +
        '              Eine Spende motiviert mich auch dazu, die Software weiter zu verbessern und auszubauen.\n' +
        '            </p>\n' +
        '            <p>\n' +
        '              Du kannst über GitHub Sponsors oder PayPal spenden. Du kannst einen Betrag wählen, den du für\n' +
        '              angemessen hältst.\n' +
        '              Wenn du mich und meine Arbeit über einen längeren Zeitraum unterstützen möchtest, können auch monatliche Spenden getätigt werden.\n' +
        '            </p>',
      action_box: {
        donated: {
          headline: 'Vielen Dank für deine Spende! (Vielleicht)',
          caption: 'Das kommt seltener vor, als Sie vielleicht denken. Vielen Dank!',
          close_btn: 'Disclaimer schließen',
          revert_btn: 'Ich möchte wieder daran erinnert werden'
        },
        not_donated: {
          headline: 'Vielen Dank!',
          donated_btn: 'Ich habe gespendet',
          close_btn: 'Nein, erinnere mich später'
        },
        lying_is_no_nice: {
          headline: 'Es fühlt sich besser an, diesen Button zu klicken, wenn man gespendet hat.',
          confirm_btn: 'Bestätigen',
          checkbox: 'Ich möchte diesen Hinweis nicht noch einmal sehen',
          go_back: 'Zurück'
        }
      }
    },
    settings_appearance: {
      language: 'Sprache',
      recipe_page_size: 'Rezepte pro Seite',
      recipe_page_size_option: '{nr} Rezepte',
      save_btn_label: 'Speichern',
      notifications: {
        settings_updated: 'Einstellungen aktualisiert!'
      },
      colors: {
        interface: {
          headline: 'Interface',
          header: 'Kopfzeile',
          sidebar: 'Seitenleiste',
          background: 'Hintergrund',
          btn_primary: 'Button / Primär',
          btn_navigation_active: 'Seitenleiste / Navigation aktiv',
          card_header: 'Karte / Kopfzeile',
          card_body: 'Karte / Inhalt',
          card_item_group: 'Karte / Elementgruppe'
        },
        simple_view: {
          headline: 'Farben für einfache Ansicht',
          header: 'Kopfzeile + Fußzeile',
          sidebar: 'Seitenleiste',
          background: 'Hintergrund',
          btn_primary: 'Button / Primär',
          btn_navigation: 'Button / Navigation',
          btn_navigation_active: 'Button / Navigation aktiv',
          cocktail_progress: 'Banner für Cocktail-Fortschritt',
          card: 'Karte'
        }
      }
    },
    collection_selection_table: {
      col: {
        name: 'Name',
        description: 'Beschreibung',
        size: 'Anzahl Rezepte'
      },
      page_select_all: 'Alle auf der Seite auswählen',
      page_deselect_all: 'Alle auf der Seite abwählen',
      search_label: 'Listen durchsuchen...',
      no_collections_found: 'Keine Listen gefunden',
      available_collections: 'Verfügbare Listen'
    },
    recipe_selection_table: {
      col: {
        name: 'Name',
        categories: 'Kategorien',
        ingredient_count: 'Zutaten',
        alcohol_free: 'Alkoholfrei',
        alcohol_free_yes: 'Ja',
        alcohol_free_no: 'Nein'
      },
      page_select_all: 'Alle auf der Seite auswählen',
      page_deselect_all: 'Alle auf der Seite abwählen',
      search_label: 'Rezepte durchsuchen...',
      no_recipes_found: 'Keine Rezepte gefunden',
      available_recipes: 'Verfügbare Rezepte',
      loading_recipes: 'Rezepte werden geladen...'
    }
  },
  constants: {
    recipe_order_options: {
      name_asc: 'Name aufsteigend',
      name_desc: 'Name absteigend',
      last_update_asc: 'Letztes Update (aufsteigend)',
      last_update_desc: 'Letztes Update (absteigend)'
    },
    event_action_trigger_display_names: {
      cocktail_prod_started: 'Cocktail-Produktion gestartet',
      cocktail_prod_manual_interaction_requested: 'Manuelle Interaktion bei Cocktail-Produktion angefordert',
      cocktail_prod_manual_interaction_completed: 'Manuelle Interaktion bei Cocktail-Produktion abgeschlossen',
      cocktail_prod_finished: 'Cocktail-Produktion beendet',
      cocktail_prod_canceled: 'Cocktail-Produktion abgebrochen',
      application_started: 'Anwendung gestartet'
    }
  }

}
