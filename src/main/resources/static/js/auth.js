$(document).ready(function () {
    $("#loginForm").submit(function (event) {
        event.preventDefault(); // Предотвращаем отправку формы
        $.ajax({
            url: "/login",
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("X-XSRF-TOKEN", csrfToken);
            },
            data: {
                username: $("#username").val(),
                password: $("#password").val(),
                remember: $("#remember").is(":checked")
            },
            success: function (data) {
                window.location.href = "/staff/menu";
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // Обработка ошибки
                $("#errorBlock").text("Error: " + textStatus).fadeIn(500).delay(5000).fadeOut(500)
            }
        });
    });
});
