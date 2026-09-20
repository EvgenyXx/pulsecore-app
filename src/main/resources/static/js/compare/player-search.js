// js/compare/player-search.js

import { searchPlayers } from './data-loader.js';

export class PlayerSearch {
    constructor(inputId, dropdownId, onSelect) {
        this.input = document.getElementById(inputId);
        this.dropdown = document.getElementById(dropdownId);
        this.onSelect = onSelect;
        this.allPlayers = [];

        if (!this.input || !this.dropdown) return;

        this.timeout = null;
        this.page = 0;
        this.size = 20;
        this.hasMore = false;
        this.loading = false;
        this.currentQuery = '';

        // Оборачиваем input в wrapper с кнопкой очистки
        this.setupClearButton();

        this.input.addEventListener('input', () => this.handleInput());
        this.input.addEventListener('focus', () => this.handleInput());

        this.dropdown.addEventListener('scroll', () => {
            if (this.loading || !this.hasMore) return;
            const { scrollTop, scrollHeight, clientHeight } = this.dropdown;
            if (scrollTop + clientHeight >= scrollHeight - 40) {
                this.loadMore();
            }
        });

        document.addEventListener('click', (e) => {
            if (!this.input.contains(e.target) && !this.dropdown.contains(e.target)) {
                this.hide();
            }
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') this.hide();
        });
    }

    setupClearButton() {
        // Создаём обёртку
        const wrapper = document.createElement('div');
        wrapper.className = 'player-search-input-wrapper';

        // Переносим input в обёртку
        this.input.parentNode.insertBefore(wrapper, this.input);
        wrapper.appendChild(this.input);

        // Кнопка очистки
        const clearBtn = document.createElement('button');
        clearBtn.type = 'button';
        clearBtn.className = 'player-search-clear hidden';
        clearBtn.setAttribute('aria-label', 'Очистить');
        clearBtn.innerHTML = `
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
        `;

        wrapper.appendChild(clearBtn);
        this.clearBtn = clearBtn;

        clearBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            this.clear();
        });
    }

    clear() {
        this.input.value = '';
        this.currentQuery = '';
        this.page = 0;
        this.hasMore = false;
        this.dropdown.innerHTML = '';
        this.hide();
        this.updateClearButton();

        // Сообщаем наверх, что игрок сброшен
        if (this.onSelect) this.onSelect(null);
    }

    setPlayers(players) {
        this.allPlayers = players || [];
    }

    setValue(value) {
        if (this.input) {
            this.input.value = value || '';
            this.updateClearButton();
        }
    }

    updateClearButton() {
        if (!this.clearBtn) return;
        if (this.input.value.length > 0) {
            this.clearBtn.classList.remove('hidden');
        } else {
            this.clearBtn.classList.add('hidden');
        }
    }

    handleInput() {
        this.updateClearButton();

        clearTimeout(this.timeout);
        const q = this.input.value.trim();

        if (q.length < 2) {
            this.hide();
            return;
        }

        this.timeout = setTimeout(() => {
            this.currentQuery = q;
            this.page = 0;
            this.hasMore = false;
            this.dropdown.innerHTML = '';
            this.search();
        }, 300);
    }

    async search() {
        if (this.loading) return;
        this.loading = true;

        try {
            const data = await searchPlayers(this.currentQuery, this.page, this.size);
            this.hasMore = !data.last;
            this.append(data.content, this.page === 0);
            this.page++;
        } catch (e) {
            console.error('Search error:', e);
        } finally {
            this.loading = false;
        }
    }

    loadMore() {
        this.search();
    }

    isRegistered(name) {
        return this.allPlayers.some(p =>
            p.playerName.toLowerCase() === name.toLowerCase()
        );
    }

    append(names, clear) {
        if (clear) this.dropdown.innerHTML = '';

        if (!names.length && this.page === 0) {
            this.dropdown.innerHTML = '<div class="player-search-empty">Ничего не найдено</div>';
            this.dropdown.classList.remove('hidden');
            return;
        }

        const html = names.map(name => {
            const registered = this.isRegistered(name);
            return `
                <div class="player-search-item" data-name="${this.escape(name)}" data-registered="${registered}">
                    <span class="player-search-name">${this.escape(name)}</span>
                    ${registered ? '<span class="player-search-badge" title="Есть в системе">✓</span>' : ''}
                </div>
            `;
        }).join('');

        this.dropdown.insertAdjacentHTML('beforeend', html);

        const items = this.dropdown.querySelectorAll('.player-search-item:not([data-bound])');
        items.forEach(item => {
            item.setAttribute('data-bound', '1');
            item.addEventListener('click', () => {
                const name = item.dataset.name;
                this.input.value = name;
                this.updateClearButton();
                this.hide();
                this.onSelect(name);
            });
        });

        const existingLoader = this.dropdown.querySelector('.player-search-loader');
        if (existingLoader) existingLoader.remove();

        if (this.hasMore) {
            this.dropdown.insertAdjacentHTML('beforeend',
                '<div class="player-search-loader">Загрузка...</div>');
        }

        this.dropdown.classList.remove('hidden');
    }

    hide() {
        this.dropdown.classList.add('hidden');
    }

    escape(str) {
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }
}