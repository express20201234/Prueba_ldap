/*global PrimeFaces:false */
/*global PF:false */
function escapeRegExp(str) {
    return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}
function escapeClientId(id) {
    return "#" + escapeRegExp(id);
}
function replaceAll(str, find, replace) {
    return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}
function getPfDatatableWidget(tableId) {
    return pfWidgetById(tableId, PrimeFaces.widget.DataTable);
}
function pfWidgetById(elementId, widgetType) {
    var ret = undefined;
    for (var propertyName in PrimeFaces.widgets) {
        if (PrimeFaces.widgets[propertyName] instanceof widgetType) {
            if (PF(propertyName).id.indexOf(elementId) !== -1) {
                ret = PF(propertyName);
                break;
            }
        }
    }
    return ret;
}
function handleDialogs() {
    fixDialogs();
    jQuery('html, body').animate({scrollTop: 90}, 500);

}

function handleDialogsMove() {
    fixDialogs();
}

function fixDialogs() {
    jQuery('div.ui-dialog').each(function () {
        var $this = jQuery(this);
        var pfDlg = pfWidgetById($this.attr('id'), PrimeFaces.widget.Dialog);
        if (pfDlg !== undefined && pfDlg.isVisible()) {
            var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
            var left = (width - $this.width()) / 2;
            $this.css('position', 'absolute');
            $this.css('left', left);
            if ($this.position().top < 90) {
                $this.css('top', '90px');
            } else if ($this.position().top > 400) {
                $this.css('top', '90px');
            }
        }
    });

    jQuery('.ui-widget-overlay').on('click', function (e) {
        e.preventDefault();
        e.stopImmediatePropagation();
        jQuery('div.ui-dialog').each(function () {
            var $this = jQuery(this);
            var pfDialog = pfWidgetById($this.attr('id'), PrimeFaces.widget.Dialog);
            if (pfDialog !== undefined && pfDialog !== null) {
                if (pfDialog.isVisible()) {
                    pfDialog.hide();
                    pfDialog.initPosition();
                }
            }
        });
    });
}
function hasFailed(xhr, args) {
    try {
        PF('statusDialog').hide();
    } catch (e) {
        console.log(e);
    }
    var containsError = xhr.responseText.indexOf('summary:"ERROR"') !== -1
            || xhr.responseText.indexOf("severity:'error'") !== -1
            || xhr.responseText.indexOf("severity:'ERROR") !== -1
            || xhr.responseText.indexOf('severity:"error') !== -1
            || xhr.responseText.indexOf('severity:"ERROR') !== -1;
    var containsWarning = xhr.responseText.indexOf("severity:'warn'") !== -1;
    return (args.validationFailed || containsError || containsWarning);
}
function handleSubmit(xhr, status, args, dialog, successDialog, errorDialog) {
    successDialog = (successDialog === undefined || successDialog === null || successDialog === '') ? undefined : successDialog;
    errorDialog = (errorDialog === undefined || errorDialog === null || errorDialog === '') ? undefined : errorDialog;
    var shakeit = (successDialog !== undefined && typeof successDialog === 'boolean' ? successDialog : true);
    var pfDialog = PF(dialog);
    if (hasFailed(xhr, args)) {
        var jqDialog = jQuery(escapeClientId(pfDialog.id));
        if (jqDialog !== undefined && jqDialog !== null) {
            if (!pfDialog.isVisible()) {
                pfDialog.show();
            }
            if (shakeit) {
                jqDialog.effect('shake', {
                    times: 5
                }, 500);
            }
        }
        if (errorDialog !== undefined) {
            PF(errorDialog).show();
        }
    } else {
        if (pfDialog !== undefined && pfDialog !== null && pfDialog.isVisible()) {
            pfDialog.hide();
        }
        if (successDialog !== undefined && typeof successDialog !== 'boolean' && status === 'success') {
            PF(successDialog).show();
        }
    }
}