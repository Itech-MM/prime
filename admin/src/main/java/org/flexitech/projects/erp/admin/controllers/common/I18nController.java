package org.flexitech.projects.erp.admin.controllers.common;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/i18n")
public class I18nController {

    private final String basename;

    public I18nController(@Value("${spring.messages.basename:messages}") String basename) {
        this.basename = basename.split(",")[0].trim();
    }

    @GetMapping("/messages")
    public ResponseEntity<Map<String, String>> messages(
            @RequestParam(name = "prefix", required = false) List<String> prefixes, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(basename, locale,
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
        Map<String, String> result = new TreeMap<>();
        for (String key : Collections.list(bundle.getKeys())) {
            if (prefixes == null || prefixes.isEmpty() || prefixes.stream().anyMatch(key::startsWith)) {
                result.put(key, bundle.getString(key));
            }
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES).cachePrivate())
                .header(HttpHeaders.VARY, HttpHeaders.COOKIE)
                .body(result);
    }
}