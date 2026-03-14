
document.addEventListener("DOMContentLoaded", function () {
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