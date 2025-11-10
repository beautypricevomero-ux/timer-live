const selectionPanel = document.querySelector('.panel--selection');
const countdownPanel = document.querySelector('.panel--countdown');
const presetButtons = document.querySelectorAll('.preset-card');
const backButton = document.querySelector('[data-action="back"]');
const resetButton = document.querySelector('[data-action="reset"]');
const timeReadout = document.querySelector('.time-readout');
const timeCaption = document.querySelector('.time-caption');
const timeWrapper = document.querySelector('.time-wrapper');
const statusLabel = document.querySelector('.status-label');

let intervalId = null;
let totalSeconds = 0;
let remainingSeconds = 0;

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
};

const setActivePanel = (panel) => {
  if (panel === 'countdown') {
    selectionPanel.classList.remove('is-active');
    countdownPanel.classList.add('is-active');
  } else {
    countdownPanel.classList.remove('is-active');
    selectionPanel.classList.add('is-active');
  }
};

const clearTimer = () => {
  if (intervalId) {
    clearInterval(intervalId);
    intervalId = null;
  }
};

const stopTimer = () => {
  clearTimer();
  remainingSeconds = 0;
  timeReadout.textContent = '00:00';
  timeCaption.textContent = "L'offerta è terminata!";
  timeWrapper.classList.remove('is-urgent');
  timeWrapper.classList.add('is-complete');
  statusLabel.textContent = 'Promo conclusa';
};

const updateUrgency = () => {
  if (remainingSeconds <= 0) {
    stopTimer();
    return;
  }

  const progress = remainingSeconds / totalSeconds;
  if (progress <= 0.4) {
    timeWrapper.classList.add('is-urgent');
    statusLabel.textContent = 'Ultimi secondi';
    timeCaption.textContent = 'Spingi le offerte, siamo al limite!';
  } else {
    timeWrapper.classList.remove('is-urgent');
    timeWrapper.classList.remove('is-complete');
    statusLabel.textContent = 'Promo in corso';
    timeCaption.textContent = 'L\'offerta esplode ora!';
  }
};

const startTimer = (seconds) => {
  clearTimer();
  totalSeconds = seconds;
  remainingSeconds = seconds;
  timeReadout.textContent = formatTime(remainingSeconds);
  timeWrapper.classList.remove('is-complete');
  timeCaption.textContent = "L'offerta esplode ora!";
  statusLabel.textContent = 'Promo in corso';

  intervalId = setInterval(() => {
    remainingSeconds -= 1;
    if (remainingSeconds <= 0) {
      timeReadout.textContent = '00:00';
      stopTimer();
      return;
    }
    timeReadout.textContent = formatTime(remainingSeconds);
    updateUrgency();
  }, 1000);

  updateUrgency();
};

presetButtons.forEach((btn) => {
  btn.addEventListener('click', () => {
    const seconds = parseInt(btn.dataset.duration, 10);
    setActivePanel('countdown');
    startTimer(seconds);
  });
});

backButton.addEventListener('click', () => {
  clearTimer();
  setActivePanel('selection');
  timeReadout.textContent = '00:00';
  timeCaption.textContent = 'Scegli il prossimo countdown';
  statusLabel.textContent = 'Pronti a partire';
  timeWrapper.classList.remove('is-urgent', 'is-complete');
});

resetButton.addEventListener('click', () => {
  if (totalSeconds > 0) {
    startTimer(totalSeconds);
  }
});

// Prevent accidental double-tap zoom on touch devices
let lastTouchEnd = 0;
document.addEventListener('touchend', (event) => {
  const now = Date.now();
  if (now - lastTouchEnd <= 400) {
    event.preventDefault();
  }
  lastTouchEnd = now;
});
