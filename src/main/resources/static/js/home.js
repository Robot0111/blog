function updateLayout() {
    const nav = document.getElementById("nav")
    const tool = document.getElementById("markdownTool")
    const footer = document.querySelector("footer");

    const navHeight = nav ? nav.offsetHeight : 0;
    const toolHeight = (nav && tool) ? tool.offsetHeight : 0;
    const footerHeight = footer ? nav.offsetHeight : 0;

    document.documentElement.style.setProperty(
        "--nav-height",
        navHeight + toolHeight + footerHeight + "px"
    );
}

// 页面加载 + 窗口变化都执行
window.addEventListener("load", updateLayout);
window.addEventListener("resize", updateLayout);
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
     extracted(savedTheme);
    updateIcon();

    // ===== 3. 点击切换 =====
    toggleBtn.addEventListener("click", function () {

        const currentTheme = html.getAttribute("data-bs-theme");
        const newTheme = currentTheme === "dark" ? "light" : "dark";

        html.setAttribute("data-bs-theme", newTheme);
        localStorage.setItem("theme", newTheme);

        extracted(newTheme);
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

    function extracted(Theme) {
        const link = document.getElementById("change-codeTheme");
        if (link) {
            link.href = Theme === "dark"
                ? "/webjars/highlightjs/11.11.1/styles/github-dark.min.css"
                : "/webjars/highlightjs/11.11.1/styles/github.min.css";
        }
    }
});