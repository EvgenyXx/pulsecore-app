// js/index.js

async function checkAuth() {
    try {
        const r = await fetch("/api/player/me", { credentials: "same-origin" });
        if (r.ok) window.location.href = "/dashboard";
    } catch (e) {}
}

function hideAll() {
    ["loginForm", "registerForm", "verifyForm", "forgotForm", "resetForm"].forEach(id => {
        document.getElementById(id).classList.add("hidden");
    });
}

function showLogin() {
    hideAll();
    document.getElementById("loginForm").classList.remove("hidden");
    document.getElementById("loginError").classList.add("hidden");
}

function showRegister() {
    hideAll();
    document.getElementById("registerForm").classList.remove("hidden");
    document.getElementById("regError").classList.add("hidden");
}

function showVerify(email) {
    hideAll();
    document.getElementById("verifyForm").classList.remove("hidden");
    document.getElementById("verifyEmailDisplay").textContent = email;
    clearCodeInputs("verifyCodeInputs");
}

function showForgot() {
    hideAll();
    document.getElementById("forgotForm").classList.remove("hidden");
    document.getElementById("forgotError").classList.add("hidden");
    document.getElementById("forgotEmail").value = "";
}

function showReset(email) {
    hideAll();
    document.getElementById("resetForm").classList.remove("hidden");
    document.getElementById("resetEmailDisplay").textContent = email;
    clearCodeInputs("resetCodeInputs");
    document.getElementById("resetNewPassword").value = "";
    document.getElementById("resetNewPassword2").value = "";
    document.getElementById("resetError").classList.add("hidden");
}

function setupCodeInputs(id) {
    const container = document.getElementById(id);
    if (!container) return;
    const inputs = container.querySelectorAll("input");
    inputs.forEach((inp, i) => {
        inp.addEventListener("input", (e) => {
            let v = e.target.value.replace(/[^0-9]/g, "");
            e.target.value = v;
            if (v && i < inputs.length - 1) inputs[i + 1].focus();
        });
        inp.addEventListener("keydown", (e) => {
            if (e.key === "Backspace" && !e.target.value && i > 0) inputs[i - 1].focus();
        });
        inp.addEventListener("paste", (e) => {
            e.preventDefault();
            let paste = (e.clipboardData || window.clipboardData).getData("text").replace(/[^0-9]/g, "").slice(0, inputs.length);
            paste.split("").forEach((c, j) => {
                if (inputs[j]) inputs[j].value = c;
            });
        });
    });
}

function getCodeFromInputs(id) {
    const container = document.getElementById(id);
    if (!container) return "";
    return Array.from(container.querySelectorAll("input")).map(i => i.value).join("");
}

function clearCodeInputs(id) {
    const container = document.getElementById(id);
    if (!container) return;
    container.querySelectorAll("input").forEach(i => i.value = "");
    if (container.querySelector("input")) container.querySelector("input").focus();
}

function togglePassword(id, btn) {
    const inp = document.getElementById(id);
    if (!inp) return;
    const isPwd = inp.type === "password";
    inp.type = isPwd ? "text" : "password";
    btn.textContent = isPwd ? "Скрыть" : "Показать";
}

async function login() {
    const email = document.getElementById("emailInput").value.trim();
    const password = document.getElementById("passwordInput").value;
    const err = document.getElementById("loginError");
    err.classList.add("hidden");

    if (!email || !password) {
        err.textContent = "Заполните все поля";
        err.classList.remove("hidden");
        return;
    }

    try {
        const r = await fetch("/api/player/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "same-origin",
            body: JSON.stringify({ email, password })
        });

        if (!r.ok) {
            const data = await r.json();
            if (data.type === "OAUTH_ONLY") {
                err.innerHTML = `${data.message}<br><br><a href="/oauth2/authorization/yandex" style="color: #ff9f0a;">Войти через Яндекс</a>`;
            } else {
                err.textContent = data.message || "Неверный email или пароль";
            }
            err.classList.remove("hidden");
            return;
        }

        window.location.href = "/dashboard";
    } catch (e) {
        err.textContent = "Ошибка соединения";
        err.classList.remove("hidden");
    }
}

async function register() {
    const ln = document.getElementById("regLastName").value.trim();
    const fn = document.getElementById("regFirstName").value.trim();
    const email = document.getElementById("regEmail").value.trim();
    const password = document.getElementById("regPassword").value;
    const err = document.getElementById("regError");
    err.classList.add("hidden");

    if (!ln || !fn || !email || !password) {
        err.textContent = "Заполните все поля";
        err.classList.remove("hidden");
        return;
    }

    if (password.length < 6) {
        err.textContent = "Пароль минимум 6 символов";
        err.classList.remove("hidden");
        return;
    }

    const btn = document.getElementById("regBtn");
    btn.disabled = true;
    btn.textContent = "Загрузка...";

    try {
        const r = await fetch("/api/player/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name: ln + " " + fn, email, password })
        });

        if (!r.ok) {
            const e = await r.json();
            if (e.type === "OAUTH_EMAIL") {
                err.innerHTML = `${e.message}<br><br><a href="/oauth2/authorization/yandex" style="color: #ff9f0a;">Войти через Яндекс</a>`;
            } else {
                err.textContent = e.message || "Ошибка регистрации";
            }
            err.classList.remove("hidden");
            btn.disabled = false;
            btn.textContent = "Зарегистрироваться";
            return;
        }

        showVerify(email);
    } catch (e) {
        err.textContent = "Ошибка соединения";
        err.classList.remove("hidden");
    } finally {
        btn.disabled = false;
        btn.textContent = "Зарегистрироваться";
    }
}

async function verifyEmail() {
    const email = document.getElementById("regEmail").value.trim();
    const password = document.getElementById("regPassword").value;
    const code = getCodeFromInputs("verifyCodeInputs");
    const err = document.getElementById("verifyError");
    err.classList.add("hidden");

    if (!code || code.length !== 6) {
        err.textContent = "Введите код полностью";
        err.classList.remove("hidden");
        return;
    }

    const btn = document.getElementById("verifyBtn");
    btn.disabled = true;
    btn.textContent = "Проверка...";

    try {
        const vr = await fetch("/api/player/verify-email", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, code })
        });

        if (!vr.ok) {
            err.textContent = "Неверный код";
            err.classList.remove("hidden");
            btn.disabled = false;
            btn.textContent = "Подтвердить";
            return;
        }

        const lr = await fetch("/api/player/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "same-origin",
            body: JSON.stringify({ email, password })
        });

        if (!lr.ok) {
            err.textContent = "Ошибка входа";
            err.classList.remove("hidden");
            btn.disabled = false;
            btn.textContent = "Подтвердить";
            return;
        }

        window.location.href = "/dashboard";
    } catch (e) {
        err.textContent = "Ошибка соединения";
        err.classList.remove("hidden");
        btn.disabled = false;
        btn.textContent = "Подтвердить";
    }
}

async function forgotPassword() {
    const email = document.getElementById("forgotEmail").value.trim();
    const err = document.getElementById("forgotError");
    err.classList.add("hidden");

    if (!email) {
        err.textContent = "Введите email";
        err.classList.remove("hidden");
        return;
    }

    const btn = document.getElementById("forgotBtn");
    btn.disabled = true;
    btn.textContent = "Отправка...";

    try {
        const r = await fetch("/api/player/forgot-password", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email })
        });

        if (!r.ok) {
            err.textContent = "Ошибка отправки";
            err.classList.remove("hidden");
            btn.disabled = false;
            btn.textContent = "Отправить код";
            return;
        }

        showReset(email);
    } catch (e) {
        err.textContent = "Ошибка соединения";
        err.classList.remove("hidden");
    } finally {
        btn.disabled = false;
        btn.textContent = "Отправить код";
    }
}

async function resetPassword() {
    const code = getCodeFromInputs("resetCodeInputs");
    const np = document.getElementById("resetNewPassword").value;
    const np2 = document.getElementById("resetNewPassword2").value;
    const err = document.getElementById("resetError");
    err.classList.add("hidden");

    if (!code || code.length !== 6) {
        err.textContent = "Введите код полностью";
        err.classList.remove("hidden");
        return;
    }

    if (!np || np.length < 6) {
        err.textContent = "Пароль минимум 6 символов";
        err.classList.remove("hidden");
        return;
    }

    if (np !== np2) {
        err.textContent = "Пароли не совпадают";
        err.classList.remove("hidden");
        return;
    }

    const btn = document.getElementById("resetBtn");
    btn.disabled = true;
    btn.textContent = "Смена...";

    try {
        const r = await fetch("/api/player/reset-password", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ code, password: np })
        });

        if (!r.ok) {
            const e = await r.json();
            err.textContent = e.message || "Неверный код";
            err.classList.remove("hidden");
            btn.disabled = false;
            btn.textContent = "Сменить пароль";
            return;
        }

        document.getElementById("resetForm").innerHTML = `
            <p style="color: #30d158; font-weight: 600; text-align: center; margin-bottom: 12px;">Пароль изменён</p>
            <p style="color: #98989d; font-size: 0.85rem; text-align: center; margin-bottom: 16px;">Войдите с новым паролем</p>
            <button onclick="showLogin()" class="auth-btn auth-btn-primary">Войти</button>
        `;
    } catch (e) {
        err.textContent = "Ошибка соединения";
        err.classList.remove("hidden");
        btn.disabled = false;
        btn.textContent = "Сменить пароль";
    }
}

// Инициализация
checkAuth();
setupCodeInputs("verifyCodeInputs");
setupCodeInputs("resetCodeInputs");

// Экспорт для onclick
window.showLogin = showLogin;
window.showRegister = showRegister;
window.showVerify = showVerify;
window.showForgot = showForgot;
window.showReset = showReset;
window.togglePassword = togglePassword;
window.login = login;
window.register = register;
window.verifyEmail = verifyEmail;
window.forgotPassword = forgotPassword;
window.resetPassword = resetPassword;