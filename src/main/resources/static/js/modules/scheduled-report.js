// modules/scheduled-report.js

export function setCachedReports(reports) {
    // Оставляем для дашборда (бейдж)
}

export function renderReportBadge(reports) {
    const pending = reports.filter(r => r.status === 'PENDING');

    if (pending.length > 0) {
        return `<span class="pro-badge report-badge" onclick="openScheduledReports()">
            <svg class="report-badge-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                <polyline points="22,6 12,13 2,6"/>
            </svg>
            <span class="report-badge-count">${pending.length}</span>
        </span>`;
    }
    return '';
}

function renderScheduledReportsSheet(reports) {
    const pending = reports.filter(r => r.status === 'PENDING');

    if (pending.length === 0) {
        return `<div class="text-center py-8">
            <span class="text-4xl">📭</span>
            <p class="text-zinc-400 text-sm mt-3">Нет запланированных отчётов</p>
        </div>`;
    }

    const items = pending.map(r => `
        <div class="report-item" data-id="${r.id}">
            <div class="report-item-period">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#a78bfa" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/></svg>
                <span>${formatDate(r.dateFrom)} – ${formatDate(r.dateTo)}</span>
            </div>
            <div class="report-item-schedule">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#a1a1aa" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                <span>${formatDateTime(r.scheduledAt)}</span>
            </div>
            <button onclick="cancelReport('${r.id}')" class="report-cancel-btn">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                Отменить
            </button>
        </div>
    `).join('');

    return `<h3 class="report-sheet-title">📬 Запланировано · ${pending.length}</h3>${items}`;
}

export function renderReportForm() {
    return `
        <div class="widget-card rounded-2xl p-4 mb-4">
            <h3 class="text-sm font-semibold text-indigo-300 mb-3">📅 Заказать отчёт на почту</h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
                <div><label class="text-xs text-zinc-400 mb-1 block">📅 Дата с</label><input id="reportDateStart" type="text" class="flatpickr-input" placeholder="Выберите дату"></div>
                <div><label class="text-xs text-zinc-400 mb-1 block">📅 Дата по</label><input id="reportDateEnd" type="text" class="flatpickr-input" placeholder="Выберите дату"></div>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 mb-4">
                <div><label class="text-xs text-zinc-400 mb-1 block">📅 Дата отправки</label><input id="reportScheduledDate" type="text" class="flatpickr-input" placeholder="Выберите дату"></div>
                <div><label class="text-xs text-zinc-400 mb-1 block">⏰ Время отправки</label><input id="reportScheduledTime" type="time" class="input"></div>
            </div>
            <button onclick="submitReport()" id="submitReportBtn" class="btn-gold w-full">📬 Оформить отчёт</button>
            <p id="reportFormMsg" class="text-xs text-center mt-2 hidden"></p>
        </div>
    `;
}

function formatDate(dateStr) {
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
}

function formatDateTime(dateStr) {
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' }) + ' в ' +
        d.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' });
}

window.cancelReport = async function(id) {
    await fetch(`/api/player/reports/${id}/cancel`, { method: 'DELETE', credentials: 'same-origin' });
    const card = document.querySelector(`.report-item[data-id="${id}"]`);
    if (card) {
        card.style.transition = 'opacity 0.3s, transform 0.3s';
        card.style.opacity = '0';
        card.style.transform = 'scale(0.95)';
        setTimeout(() => {
            fetch('/api/player/reports/pending', { credentials: 'same-origin' })
                .then(r => r.json())
                .then(reports => {
                    document.getElementById('reportSheetContent').innerHTML = renderScheduledReportsSheet(reports);
                    document.getElementById('reportBadge').innerHTML = renderReportBadge(reports);
                });
        }, 300);
    }
};

window.openScheduledReports = function() {
    fetch('/api/player/reports/pending', { credentials: 'same-origin' })
        .then(r => r.json())
        .then(reports => {
            document.getElementById('reportSheetContent').innerHTML = renderScheduledReportsSheet(reports);
            document.getElementById('reportDropdown').classList.add('open');
            document.getElementById('reportBackdrop').classList.add('open');
        });
};

window.closeScheduledReports = function() {
    document.getElementById('reportDropdown').classList.remove('open');
    document.getElementById('reportBackdrop').classList.remove('open');
};

window.submitReport = async function() {
    const dateStart = document.getElementById('reportDateStart').value;
    const dateEnd = document.getElementById('reportDateEnd').value;
    const scheduledDate = document.getElementById('reportScheduledDate').value;
    const scheduledTime = document.getElementById('reportScheduledTime').value;
    const msg = document.getElementById('reportFormMsg');
    const btn = document.getElementById('submitReportBtn');

    if (!dateStart || !dateEnd || !scheduledDate || !scheduledTime) {
        msg.textContent = '❌ Заполните все поля';
        msg.className = 'text-xs text-center mt-2 text-red-400';
        msg.classList.remove('hidden');
        return;
    }

    const scheduledAt = `${scheduledDate}T${scheduledTime}:00`;

    btn.disabled = true;
    btn.innerHTML = '⏳ Отправка...';

    try {
        const res = await fetch('/api/player/reports', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'same-origin',
            body: JSON.stringify({ dateFrom: dateStart, dateTo: dateEnd, scheduledAt })
        });

        if (res.ok) {
            msg.textContent = '✅ Отчёт запланирован!';
            msg.className = 'text-xs text-center mt-2 text-emerald-400';
            msg.classList.remove('hidden');
            btn.innerHTML = '✅ Готово';
        } else {
            throw new Error();
        }
    } catch(e) {
        msg.textContent = '❌ Ошибка';
        msg.className = 'text-xs text-center mt-2 text-red-400';
        msg.classList.remove('hidden');
        btn.disabled = false;
        btn.innerHTML = '📬 Оформить отчёт';
    }
};