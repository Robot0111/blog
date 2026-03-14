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

const editor = document.getElementById("editor")

const preview = document.getElementById("preview")


editor.addEventListener("input", render)

render()

function render() {

    preview.innerHTML = md.render(editor.value)

}

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

    editor.focus()

    render()

}

function insert(text) {

    const s = editor.selectionStart

    const val = editor.value

    editor.value =
        val.substring(0, s) +
        text +
        val.substring(s)

    editor.focus()

    render()

}

function heading(level) {

    insert("#".repeat(level) + " ")

}

function codeBlock() {

    insert("```\n\n```")

}

function link() {

    insert("[文字](https://)")

}

function emoji() {

    insert(":smile:")

}

function undo() {

    document.execCommand("undo")

}

function redo() {

    document.execCommand("redo")

}

function fullscreen() {

    document.body.classList.toggle("fullscreen")

}