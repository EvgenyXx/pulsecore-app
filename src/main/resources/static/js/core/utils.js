export function formatMoney(v) {
    if (v == null) return "0 ₽";
    return new Intl.NumberFormat("ru-RU").format(Math.round(v)) + " ₽";
}

export function formatDateShort(dateStr) {
    if (!dateStr) return '—';
    const parts = dateStr.split('-');
    return parts[2] + '.' + parts[1];
}

export function capitalizeName(name) {
    if (!name) return '';
    return name
        .split(' ')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
        .join(' ');
}