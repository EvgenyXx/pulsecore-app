// js/compare/period.js

export function getPeriodDates(period) {
    const today = new Date();
    let start = null;
    let end = today.toISOString().split('T')[0];
    let label = 'Всё время';

    switch (period) {
        case 'week': {
            const day = today.getDay() || 7;
            const monday = new Date(today);
            monday.setDate(today.getDate() - day + 1);
            start = monday.toISOString().split('T')[0];
            label = 'Неделя';
            break;
        }
        case 'month': {
            start = new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0];
            label = 'Месяц';
            break;
        }
        case 'year': {
            start = new Date(today.getFullYear(), 0, 1).toISOString().split('T')[0];
            label = 'Год';
            break;
        }
        case 'all': {
            start = null;
            end = null;
            label = 'Всё время';
            break;
        }
    }

    return { start, end, label };
}

export function buildQueryString(params) {
    const searchParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
        if (value) searchParams.append(key, value);
    });
    const query = searchParams.toString();
    return query ? '?' + query : '';
}