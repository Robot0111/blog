
document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("prevMonth").addEventListener("click", () => {

        currentDate = new Date(
            currentDate.getFullYear(),
            currentDate.getMonth() - 1,
            1
        );
        renderCalendar();
    });

    document.getElementById("nextMonth").addEventListener("click", () => {

        currentDate = new Date(
            currentDate.getFullYear(),
            currentDate.getMonth() + 1,
            1
        );

        renderCalendar();

    });

    const title = document.getElementById("calendarTitle");
    const body = document.getElementById("calendarBody");

    let currentDate = new Date();

    function renderCalendar() {

        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        title.innerText = `${year} 年 ${month + 1} 月`;

        const firstDay = new Date(year, month, 1).getDay();
        const lastDate = new Date(year, month + 1, 0).getDate();

        body.innerHTML = "";

        let row = document.createElement("tr");

        // 前面的空格
        for (let i = 0; i < firstDay; i++) {
            row.appendChild(document.createElement("td"));
        }

        for (let day = 1; day <= lastDate; day++) {

            if ((firstDay + day - 1) % 7 === 0 && day !== 1) {
                body.appendChild(row);
                row = document.createElement("tr");
            }

            const cell = document.createElement("td");
            cell.textContent = day;

            const today = new Date();

            if (
                day === today.getDate() &&
                month === today.getMonth() &&
                year === today.getFullYear()
            ) {
                cell.classList.add("table-info");
            }

            row.appendChild(cell);
        }

        body.appendChild(row);
    }

    renderCalendar();

    const html = document.documentElement;
    const toggleBtn = document.getElementById("themeToggle");
    const icon = document.getElementById("themeIcon");

    // ===== 1. 设置主题 =====
    const savedTheme = localStorage.getItem("theme");

    if (savedTheme) {
        html.setAttribute("data-bs-theme", savedTheme);
    } else {
        const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        html.setAttribute("data-bs-theme", prefersDark ? "dark" : "light");
    }


    updateIcon();

    // ===== 3. 点击切换 =====
    toggleBtn.addEventListener("click", function () {

        const currentTheme = html.getAttribute("data-bs-theme");
        const newTheme = currentTheme === "dark" ? "light" : "dark";

        html.setAttribute("data-bs-theme", newTheme);
        localStorage.setItem("theme", newTheme);

        updateIcon();
    });

    function updateIcon() {
        const currentTheme = html.getAttribute("data-bs-theme");

        if (currentTheme === "dark") {
            icon.classList.remove("bi-moon");
            icon.classList.add("bi-sun");
        } else {
            icon.classList.remove("bi-sun");
            icon.classList.add("bi-moon");
        }
    }

});