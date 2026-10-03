var AjaxFormSubmit = (function () {

    function init(formSelector) {
        $(formSelector).each(function () {
            bind($(this));
        });
    }

    function bind(form) {
        form.on('submit', function (e) {
            e.preventDefault();

            /*if (window.FormValidator && !window.FormValidator.validateAndShow(form[0])) {
                return;
            }*/

            submit(form);
        });
    }

    function submit(form) {
        var url = form.data('ajax-url');
        var payload = collect(form);

        clearServerErrors(form);
        form.find('[type="submit"]').prop('disabled', true);

        $.ajax({
            url: url,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            success: function (res) {
                form.find('[type="submit"]').prop('disabled', false);
                form.trigger('ajaxForm:success', [res]);
            },
            error: function (xhr) {
                form.find('[type="submit"]').prop('disabled', false);
                applyServerErrors(form, xhr);
                form.trigger('ajaxForm:error', [xhr]);
            }
        });
    }

    function collect(form) {
        var payload = {};
        form.find('[data-field]').each(function () {
            var el = $(this);
            var field = el.data('field');
            var type = el.data('type');
            var value = el.val();

            if (el.is(':checkbox')) {
                value = el.is(':checked');
            } else if (value === '' || value === null || value === undefined) {
                value = null;
            } else if (type === 'number') {
                value = Number(value);
            }

            payload[field] = value;
        });
        return payload;
    }

    function clearServerErrors(form) {
        form.find('[data-field]').removeClass('is-invalid');
        form.find('.select2-container .select2-selection').removeClass('is-invalid');
        form.find('[data-field-error]').text('');
    }

    function applyServerErrors(form, xhr) {
        var res = null;
        try { res = JSON.parse(xhr.responseText); } catch (e) { res = null; }

        if (xhr.status === 400 && res && res.fieldErrors) {
            Object.keys(res.fieldErrors).forEach(function (field) {
                var message = res.fieldErrors[field];
                var input = form.find('[data-field="' + field + '"]');
                var errorEl = form.find('[data-field-error="' + field + '"]');

                if (input.hasClass('select2-hidden-accessible')) {
                    input.next('.select2-container').find('.select2-selection').addClass('is-invalid');
                } else {
                    input.addClass('is-invalid');
                }
                errorEl.text(message);
            });
            showToast(res.message || 'Please fix the highlighted fields', 'error');
        } else {
            showToast((res && res.message) || xhr.responseText || 'An error occurred', 'error');
        }
    }

    return { init: init, submit: submit };
})();