package org.hbrs.se2.project.services.impl;

import com.vaadin.flow.i18n.I18NProvider;
import org.hbrs.se2.project.util.Utils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.*;

@Component
public class TranslationProvider implements I18NProvider {

    private final Logger logger = Utils.getLogger(this.getClass().getName());
    public static final String BUNDLE_PREFIX = "translate";

    public final Locale LOCALE_DE = new Locale("de");

    private final List<Locale> locales = List.of(LOCALE_DE);

    @Override
    public List<Locale> getProvidedLocales() {
        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            logger.warn("Got lang request for key with null value!");
            return "";
        }

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);

        String value;
        try {
            value = bundle.getString(key);
        } catch (final MissingResourceException e) {
            logger.warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }
}