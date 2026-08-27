// js/compare/wheel.js

export class WheelPicker {
    constructor(containerId, players, onSelect) {
        this.container = document.getElementById(containerId);
        this.players = players;
        this.onSelect = onSelect;
        this.selectedId = null;
    }

    render(selectedId = null) {
        this.selectedId = selectedId;
        this.container.innerHTML = this.players.map(p => `
            <div class="wheel-option ${p.playerId === selectedId ? 'selected' : ''}" data-player-id="${p.playerId}">
                ${p.playerName}
            </div>
        `).join('');

        this.attachEvents();
    }

    attachEvents() {
        this.container.addEventListener('scroll', () => {
            const items = this.container.querySelectorAll('.wheel-option');
            const pickerCenter = this.container.scrollTop + this.container.clientHeight / 2;

            let closestItem = null;
            let closestDistance = Infinity;

            items.forEach(item => {
                const itemCenter = item.offsetTop + item.clientHeight / 2;
                const distance = Math.abs(itemCenter - pickerCenter);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestItem = item;
                }
            });

            if (closestItem) {
                items.forEach(i => i.classList.remove('selected'));
                closestItem.classList.add('selected');
                this.selectedId = closestItem.dataset.playerId;
                const player = this.players.find(p => p.playerId === this.selectedId);
                if (player && this.onSelect) {
                    this.onSelect(player);
                }
            }
        });

        this.container.addEventListener('click', (e) => {
            const item = e.target.closest('.wheel-option');
            if (item) {
                item.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        });
    }

    scrollToPlayer(playerId) {
        const item = this.container.querySelector(`[data-player-id="${playerId}"]`);
        if (item) {
            item.scrollIntoView({ behavior: 'instant', block: 'center' });
        }
    }

    getSelectedPlayer() {
        return this.players.find(p => p.playerId === this.selectedId) || null;
    }


    destroy() {
        this.container.replaceWith(this.container.cloneNode(true));
        this.container = document.getElementById(this.container.id);
    }
}