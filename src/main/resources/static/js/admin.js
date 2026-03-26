const editor = document.getElementById("editor")
const preview = document.getElementById("preview")

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const articleId = urlParams.get('id');

    if (articleId) {
        // 说明是编辑模式
        const res = await fetch(`/api/articles/${articleId}`);
        const article = await res.json();

        // 1. 把内容塞进编辑器
        editor.value = article.CONTENT_MD;

        preview.innerHTML = article.CONTENT_HTML;

        // 2. 记录 ID，以便后续提交时知道是更新
        document.getElementById('articleId').value = article.ID;

        // 3. 在你那个“保存弹窗”弹出时，记得勾选上对应的分类和标签
        window.currentArticleData = article;
    }
});
// 初始化弹窗实例 (Bootstrap 5)
const saveModal =  bootstrap.Modal.getOrCreateInstance(document.getElementById('saveModal'));
const saveTriggerBtn = document.getElementById("save"); // 你的 floppy 图标按钮
const confirmSaveBtn = document.getElementById("confirm-save-btn"); // 弹窗内的确定按钮

// 1. 点击 floppy 按钮只负责显示弹窗
saveTriggerBtn.addEventListener('click', async () => {
    // 1. 显示加载状态（可选：比如按钮变菊花）
    saveTriggerBtn.disabled = true;

    try {
        // 加载分类标签
        await Promise.all([fetchCategoriesToSelect(), fetchTagsToCheckboxes()]);

        // 如果是编辑模式，回显数据
        const articleId = document.getElementById('articleId').value;
        if (articleId && window.currentArticleData) {
            document.getElementById('category').value = window.currentArticleData.CATEGORY_ID || '';

            const savedTagIds = window.currentArticleData.tagIds; // 后端返回的标签ID列表
            savedTagIds.forEach(tagId => {
                const checkbox = document.getElementById(`tag-${tagId}`);
                if (checkbox) checkbox.checked = true;
            });
        }

        // 正确获取实例并显示
        saveModal.show();
    } catch (e) {
        console.error("弹窗初始化失败", e);
    } finally {
        saveTriggerBtn.disabled = false;
    }
});

// 获取分类并渲染到 Select
async function fetchCategoriesToSelect() {
    const res = await fetch('/api/categories?page=1&size=100'); // 获取全部
    const data = await res.json();
    const select = document.getElementById('category');

    // 保留第一个默认选项，清空后面的
    select.innerHTML = '<option selected disabled value="">请选择一个分类...</option>';

    data.list.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item.ID;
        opt.textContent = item.NAME;
        select.appendChild(opt);
    });
}

// 获取标签并渲染为胶囊按钮
async function fetchTagsToCheckboxes() {
    const res = await fetch('/api/tags?page=1&size=100');
    const data = await res.json();
    const container = document.querySelector('.d-flex.flex-wrap.gap-2');
    container.innerHTML = ''; // 清空原有的静态 HTML

    data.list.forEach(item => {
        const html = `
            <input type="checkbox" class="btn-check tag-input" id="tag-${item.ID}" autocomplete="off" value="${item.ID}">
            <label class="btn btn-outline-secondary btn-sm rounded-pill" for="tag-${item.ID}">${item.NAME}</label>
        `;
        container.insertAdjacentHTML('beforeend', html);
    });
}

// 2. 真正的保存逻辑放在弹窗的“确定”按钮上
confirmSaveBtn.addEventListener('click', () => {
    const categoryId = document.getElementById('category').value;

    // 获取所有选中的标签 ID 列表
    const selectedTags = Array.from(document.querySelectorAll('.tag-input:checked'))
        .map(cb => parseInt(cb.value));

    if (!categoryId) {
        alert("请选择一个分类");
        return;
    }
    // 动态获取弹窗内的数据
    const payload = {
        id:document.getElementById("articleId").value,
        contentMd: document.getElementById('editor').value,
        contentHtml: document.getElementById('preview').innerHTML,
        categoryId: parseInt(categoryId), // 获取选中的 ID
        tagIds: selectedTags // 获取选中的标签数组
    };

    // 执行保存请求
    fetch('/api/articles/save', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (res.ok) {
                alert("保存成功");
                saveModal.hide(); // 保存成功后关闭弹窗
            } else {
                return res.text().then(text => { throw new Error(text) });
            }
        })
        .catch(err => {
            console.error("提交失败:", err);
            alert("提交失败，请检查控制台");
        });
});

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

function getCursorLine(textarea) {
    const value = textarea.value;
    const cursorPos = textarea.selectionStart;

    // 截取光标前的内容
    const textBefore = value.substring(0, cursorPos);

    // 统计换行数量
    return textBefore.split('\n').length - 1;
}
editor.addEventListener("keyup", syncCursor);
editor.addEventListener("click", syncCursor);
let cursorPos = 0
function syncCursor() {
    const line = getCursorLine(editor);
    const target = lineMap.get(line);
    if (!target) return;

    scrollIntoViewIfNeeded(preview, target);

    editor.addEventListener("keyup", () => {
        cursorPos = editor.selectionStart
    })

}

function scrollIntoViewIfNeeded(container, element) {
    const cTop = container.scrollTop;
    const cBottom = cTop + container.clientHeight;

    const eTop = element.offsetTop;
    const eBottom = eTop + element.offsetHeight;

    // ✅ 已经完全可见 → 不动
    if (eTop >= cTop && eBottom <= cBottom) {
        return;
    }

    // ⬆️ 在上面 → 滚到顶部
    if (eTop < cTop) {
        container.scrollTo({
            top: eTop - 20, // 留点间距
            behavior: "smooth"
        });
    }

    // ⬇️ 在下面 → 滚到下面
    else if (eBottom > cBottom) {
        container.scrollTo({
            top: eBottom - container.clientHeight + 20,
            behavior: "smooth"
        });
    }
}
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
            heading(parseInt(action[1]))
            break
        default:
            insert(action)
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

