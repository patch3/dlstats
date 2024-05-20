var csrfToken = $("meta[name='_csrf']").attr("content");

// Если токен не найден, то пытаемся получить его из cookie
if (!csrfToken) {
    csrfToken = getCookie("XSRF-TOKEN");
}

// Функция для получения cookie
function getCookie(name) {
    var cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        var cookies = document.cookie.split(';');
        for (var i = 0; i < cookies.length; i++) {
            var cookie = jQuery.trim(cookies[i]);
            // Does this cookie string begin with the name we want?
            if (cookie.substring(0, name.length + 1) === (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}