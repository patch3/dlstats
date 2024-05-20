const columnSelector = document.getElementById("columnSelector");

const filterInputNameStudent = document.getElementById("filterInputNameStudent")
const filterInputEmailStudent = document.getElementById("filterInputEmailStudent")
const filterInputTaskNum = document.getElementById("filterInputTaskNum")
const filterInputGrade = document.getElementById("filterInputGrade")
const filterInputSendAnswers = document.getElementById("filterInputSendAnswers")


columnSelector.addEventListener("change", updateTableEvent)

filterInputNameStudent.addEventListener("input", updateTableEvent)
filterInputEmailStudent.addEventListener("input", updateTableEvent)
filterInputTaskNum.addEventListener("input", updateTableEvent)
filterInputGrade.addEventListener("input", updateTableEvent)
filterInputSendAnswers.addEventListener("input", updateTableEvent)

function updateTableEvent() {
    $.ajax({
        url: '/staff/stats/filtered-data',
        type: 'post',
        contentType: 'application/json',
        beforeSend: function (xhr) {
            xhr.setRequestHeader("X-XSRF-TOKEN", csrfToken);
        },
        data: JSON.stringify({
            nameStudent: filterInputNameStudent.value,
            emailStudent: filterInputEmailStudent.value,
            taskNum: filterInputTaskNum.value,
            sendAnswers: filterInputSendAnswers.value,
            grade: filterInputGrade.value,
            sortBy: columnSelector.value,
            sortOrder: "asc"
        }),
        success: function (data) {
            $("#search-result-table tbody tr").empty();
            for (const row of data) {
                $('#search-result-table > tbody:last-child').append(
                    `<tr>
                        <th><a href="https://dl-ido.spbstu.ru/user/profile.php?id=${row.student.name}">${row.student.name}</a></th>
                        <td>${row.student.email}</td>
                        <td>${row.taskNum}</td>
                        <td>${row.grade}</td>
                        <td>${row.sendAnswers}</td>
                        <td>${row.sendDate}</td>
                    </tr>`
                );
            }
        },
        error: function (xhr, textStatus, error) {
            alert("Error: " + textStatus)
        }
    })
}