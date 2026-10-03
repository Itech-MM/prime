window.I18n = (function($) {
    const store = {};
    const loaded = {};

    function load(prefixes) {
        const pending = [].concat(prefixes).filter(function(p) { return !loaded[p]; });
        if (!pending.length) {
            return $.Deferred().resolve().promise();
        }
        return $.ajax({
            url: CONTEXT_PATH + 'api/i18n/messages',
            type: 'GET',
            data: { prefix: pending },
            traditional: true,
            dataType: 'json'
        }).done(function(data) {
            $.extend(store, data);
            pending.forEach(function(p) { loaded[p] = true; });
        });
    }

    function t(key) {
        const args = Array.prototype.slice.call(arguments, 1);
        const message = Object.prototype.hasOwnProperty.call(store, key) ? store[key] : key;
        return message.replace(/\{(\d+)\}/g, function(match, index) {
            return args[index] !== undefined ? args[index] : match;
        });
    }

    return { load: load, t: t };
})(jQuery);