// function test(){
//     const editor = document.getElementById("editor")
// const preview = document.getElementById("preview")
//     preview.innerHTML = editor.value;
// }
const md = window.markdownit({

    html: true,
    linkify: true,
    typographer: true,
    highlight: function (str, lang) {
        if (lang && hljs.getLanguage(lang)) {
            try {
                return '<pre><code class="hljs">' +
                    hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
                    '</code></pre>';
            } catch (__) {}
        }
        return '<pre><code class="hljs">' + md.utils.escapeHtml(str) + '</code></pre>';
    }
}).use(window.markdownitEmoji)
    .use(window.markdownitSub)
    .use(window.markdownitSup)
    .use(window.markdownitFootnote)
    .use(window.markdownitDeflist)
    .use(window.markdownitAbbr)
    .use(window.markdownitIns)
    .use(window.markdownitMark)
const lineMap = new Map();

function buildMap() {
    lineMap.clear();
    document.querySelectorAll("[data-line]").forEach(el => {
        lineMap.set(parseInt(el.dataset.line), el);
    });
}
md.core.ruler.push("line_numbers", function (state) {
    state.tokens.forEach(token => {
        if (token.map && token.type.endsWith("_open")) {
            token.attrSet("data-line", token.map[0]);
        }
    });
});
const editor = document.getElementById("editor")
const preview = document.getElementById("preview")
// function getCursorLine(textarea) {
//     const value = textarea.value;
//     const cursorPos = textarea.selectionStart;
//
//     // 截取光标前的内容
//     const textBefore = value.substring(0, cursorPos);
//
//     // 统计换行数量
//     return textBefore.split('\n').length - 1;
// }
// editor.addEventListener("keyup", syncCursor);
// editor.addEventListener("click", syncCursor);
// let cursorPos = 0
// function syncCursor() {
//     const line = getCursorLine(editor);
//     const target = lineMap.get(line);
//     if (!target) return;
//
//     scrollIntoViewIfNeeded(preview, target);
//
//     editor.addEventListener("keyup", () => {
//         cursorPos = editor.selectionStart
//     })
//
// }

// function scrollIntoViewIfNeeded(container, element) {
//     const cTop = container.scrollTop;
//     const cBottom = cTop + container.clientHeight;
//
//     const eTop = element.offsetTop;
//     const eBottom = eTop + element.offsetHeight;
//
//     // ✅ 已经完全可见 → 不动
//     if (eTop >= cTop && eBottom <= cBottom) {
//         return;
//     }
//
//     // ⬆️ 在上面 → 滚到顶部
//     if (eTop < cTop) {
//         container.scrollTo({
//             top: eTop - 20, // 留点间距
//             behavior: "smooth"
//         });
//     }
//
//     // ⬇️ 在下面 → 滚到下面
//     else if (eBottom > cBottom) {
//         container.scrollTo({
//             top: eBottom - container.clientHeight + 20,
//             behavior: "smooth"
//         });
//     }
// }
// 渲染
function render() {
    let text = editor.value

    // 自动修正标题格式
    text = text.replace(/^\s+(#+)/gm, (m, hashes) => {
        return hashes // 去掉所有前导空格
    })

    preview.innerHTML = md.render(text)
    // 数学公式
    renderMathInElement(preview, {
        delimiters: [
            { left: "$$", right: "$$", display: true },
            { left: "$", right: "$", display: false }
        ],
        throwOnError: false
    });
    buildMap();
}

editor.addEventListener("keydown", function (e) {
    if (e.key === "Tab") {
        e.preventDefault()
        const start = editor.selectionStart
        const end = editor.selectionEnd
        editor.value =
            editor.value.substring(0, start) +
            "    " +
            editor.value.substring(end)
        editor.selectionStart = editor.selectionEnd = start + 4
        render()
    }
})

let timer
editor.addEventListener("input", () => {
    clearTimeout(timer)
    timer = setTimeout(render, 500)
    saveHistory();
})

function wrap(start, end) {

    const s = editor.selectionStart
    const e = editor.selectionEnd
    const val = editor.value

    editor.value =
        val.substring(0, s) +
        start +
        val.substring(s, e) +
        end +
        val.substring(e)

    // ⭐ 恢复光标
    editor.selectionStart = s + start.length
    editor.selectionEnd = e + start.length

    editor.focus()
    render()
}
function heading(level) {
    const prefix = "#".repeat(level) + " "
    insert(prefix)
}
function insert(text) {
    const start = cursorPos
    const end = editor.selectionEnd
    const val = editor.value

    editor.value =
        val.substring(0, start) +
        text +
        val.substring(end)

    // ⭐ 光标移动到插入内容后面
    const newPos = start + text.length
    editor.setSelectionRange(newPos, newPos)

    editor.focus()

    render()
}

document.querySelector(".toolbar").addEventListener("mousedown", function (e) {
    const btn = e.target.closest("[data-action]")
    if (!btn) return

    e.preventDefault() // ⭐ 防止光标丢失

    const action = btn.dataset.action

    handleAction(action)
})
function handleAction(action) {
    switch (action) {

        case "bold":
            wrap("**", "**")
            break

        case "italic":
            wrap("*", "*")
            break

        case "strike":
            wrap("~~", "~~")
            break

        case "inline-code":
            wrap("`", "`")
            break

        case "code":
            insert("```\n\n```")
            break

        case "quote":
            insert("> ")
            break

        case "ul":
            insert("- ")
            break

        case "ol":
            insert("1. ")
            break

        case "task":
            insert("- [ ] ")
            break

        case "hr":
            insert("\n---\n")
            break

        case "link":
            insert("[文字](https://)")
            break

        case "emoji":
            insert(":smile:")
            break

        case "undo":
            undo()
            break

        case "redo":
            redo()
            break

        case "fullscreen":
            fullscreen()
            break

        // 标题
        case "h1":
        case "h2":
        case "h3":
        case "h4":
        case "h5":
        case "h6":
            heading(parseInt(action[1]))
            break

        default:
            console.warn("未知操作:", action)
    }
}

let history = []
let index = -1

function saveHistory() {
    history = history.slice(0, index + 1)
    history.push(editor.value)
    index++
}



function undo() {
    if (index > 0) {
        index--
        editor.value = history[index]
        render()
    }
}
function redo() {
    if (index < history.length - 1) {
        index++
        editor.value = history[index]
        render()
    }
}
function fullscreen() {
    if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen().catch(() => {})
    } else {
        document.exitFullscreen().catch(() => {})
    }
}

