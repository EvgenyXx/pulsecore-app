import { state } from '../core/state.js';
import { loadTopWeek } from './top.js';

export function switchLeague(league) {
    const leagues = ['A', 'B', 'C', 'D', 'SUPER_LEAGUE'];
    const activeIndex = league === null ? 0 : leagues.indexOf(league) + 1;
    const slider = document.querySelector('.league-slider');
    if (slider) {
        slider.className = `league-slider pos-${activeIndex}`;
    }

    document.querySelectorAll('.league-pill').forEach(pill => {
        pill.classList.remove('active');
    });
    if (event?.target) {
        event.target.classList.add('active');
    }

    loadTopWeek(league);
}

export function switchPeriod(period) {
    state.currentPeriod = period;
    const slider = document.querySelector('.period-slider');
    if (slider) {
        slider.className = `period-slider ${period === 'week' ? 'pos-0' : period === 'month' ? 'pos-1' : 'pos-2'}`;
    }

    document.querySelectorAll('.period-pill').forEach(pill => {
        pill.classList.remove('active');
    });
    if (event?.target) {
        event.target.classList.add('active');
    }

    const al = document.querySelector('.league-pill.active');
    const lg = al && al.textContent !== 'Все' ? al.textContent : null;
    loadTopWeek(lg);
}

window.switchLeague = switchLeague;
window.switchPeriod = switchPeriod;