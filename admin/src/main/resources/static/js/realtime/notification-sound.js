(function() {
    let soundEnabled = false;
    let soundUnlocked = false;
    let audio = null;

    function getAudio() {
        if (!audio) {
            audio = new Audio(CONTEXT_PATH + 'audio/noti.mp3');
            audio.volume = 0.7;
            audio.load();
        }
        return audio;
    }

    function playNewOrderSound() {
        if (!soundEnabled) return;
        if (!soundUnlocked) {
            console.log('Audio not unlocked – will play after first interaction');
            return;
        }
        const a = getAudio();
        a.currentTime = 0;
        a.play().catch(e => console.warn('Playback failed:', e));
    }

    function unlockAudio() {
        if (soundUnlocked) return;
        console.log('Attempting to unlock audio...');
        const a = getAudio();
        const originalVolume = a.volume;
        a.volume = 0;
        a.play().then(() => {
            soundUnlocked = true;
            console.log('Audio unlocked successfully');
            a.pause();
            a.currentTime = 0;
            a.volume = originalVolume;
        }).catch(e => {
            console.error('Failed to unlock audio:', e);
        });
        document.body.removeEventListener('click', unlockAudio);
    }

    function toggleSound() {
        soundEnabled = !soundEnabled;
        localStorage.setItem('soundEnabled', soundEnabled);
        const icon = document.querySelector('#soundToggleBtn i');
        if (icon) {
            icon.className = soundEnabled ? 'fas fa-volume-up' : 'fas fa-volume-mute';
        }
        if (soundEnabled && soundUnlocked) {
            playNewOrderSound();
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        const btn = document.getElementById('soundToggleBtn');
        if (btn) {
            btn.addEventListener('click', function(e) {
                e.stopPropagation();
                toggleSound();
            });
            const icon = btn.querySelector('i');
            if (icon && !soundEnabled) {
                icon.className = 'fas fa-volume-mute';
            }
        }
        document.body.addEventListener('click', unlockAudio);
    });

    window.playNewOrderSound = playNewOrderSound;
})();