function alertPF(m) {
    var dialog = PF('alertDialogWV');
    if (dialog !== undefined) {
        var $messageText = jQuery('#' + dialog.id).find('#alertDialogText');
        if ($messageText !== undefined) {
            $messageText.html(m);
        }
        dialog.show();
    }
}

function copyAny(e, m) {
    jQuery('#' + e).select();
    document.execCommand('copy');
    alertPF(m);
}

//Función para copiar toda la información del UPS seleccionado
function copyUps() {
    copyAny('copyInfoUps', '¡Información del UPS copiado!');
}

//Función para copiar toda la información del gabinete seleccionado
function copyGabinete() {
    copyAny('copyInfoGab', '¡Información del gabinete copiado!');
}


//Función para copiar toda la información del servidor seleccionado
function copyServidor() {
    copyAny('copyInfoServidor', '¡Información del Servidor copiado!');
}


//Función para copiar toda la información de la unidad de gabinete seleccionada
function CopyUGab() {
    copyAny('copyInfoUGab', '¡Información de Unidad de Gabinete copiado!');
}


//Función para copiar toda la información del componente seleccionado
function copyComponente() {
    copyAny('copyInfoComponente', '¡Información del Componente copiado!');
}

//Función para copiar toda la información del administrador seleccionado
function copyAdministrador() {
    copyAny('copyInfoAdministrador', '¡Información del Usuario copiado!');
}


//Función para copiar toda la información del contrato seleccionado
function copyContrato() {
    copyAny('copyInfoContrato', '¡Información del contrato copiado!');
}


//Función para copiar la información del servidor seleccionado en la unidad de gabinete.
function CopyInfoServidorUGab() {
    copyAny('copyInfoServidorUGab', '¡Información del registro de unidad de gabinete copiado!');
}

//Función para copiar la información del componente seleccionado en la unidad de gabinete.
function copyInfoComponenteUGab() {
    copyAny('copyInfoComponenteUGab', '¡Información del registro de unidad de gabinete copiado!');
}

//Función para copiar toda la información del registro de unidad de gabinete seleccionado
function CopyRegistroSelecUGabServidor() {
    copyAny('copyInfoRegistUGabServidor', '¡Información del registro de unidad de gabinete copiado!');
}


//Función para copiar toda la información del registro de unidad de gabinete de los componentes.
function CopyRegistroSelecUGabComponente() {
    copyAny('copyInfoRegistUGabComponente', '¡Información del registro de unidad de gabinete copiado!');
}
