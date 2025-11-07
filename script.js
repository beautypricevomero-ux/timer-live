const app = document.querySelector('.app');
const selectionPanel = document.querySelector('[data-panel="selection"]');
const timerPanel = document.querySelector('[data-panel="timer"]');
const optionButtons = Array.from(document.querySelectorAll('.timer-option'));
const backButton = document.querySelector('.action-back');
const resetButton = document.querySelector('.action-reset');
const timerRing = document.querySelector('.timer-ring');
const timerLabel = document.querySelector('.timer-label');
const timerTime = document.querySelector('.timer-time');
const timerStatus = document.querySelector('.timer-status');

let animationFrameId = null;
let finishTimestamp = 0;
let totalDuration = 0;
let timerActive = false;
let lastConfig = null;

const DEFAULT_ACCENT = '#ff9bcf';

const formatTime = (totalSeconds) => {
  const clamped = Math.max(0, Math.ceil(totalSeconds));
  const minutes = Math.floor(clamped / 60)
    .toString()
    .padStart(2, '0');
  const seconds = (clamped % 60).toString().padStart(2, '0');
  return `${minutes}:${seconds}`;
};

const setAccent = (color) => {
  document.documentElement.style.setProperty('--accent', color);
};

const cancelTimer = () => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
    animationFrameId = null;
  }
  timerActive = false;
};

const updateProgress = () => {
  const remaining = Math.max(0, finishTimestamp - performance.now());
  const secondsRemaining = remaining / 1000;
  const progress = totalDuration > 0 ? Math.min(1, 1 - remaining / (totalDuration * 1000)) : 0;

  timerRing.style.setProperty('--progress', progress.toString());
  timerTime.textContent = formatTime(secondsRemaining);

  if (remaining <= 0) {
    timerStatus.textContent = 'Offerta conclusa';
    timerRing.classList.add('completed');
    timerActive = false;
    animationFrameId = null;
    return;
  }

  animationFrameId = requestAnimationFrame(updateProgress);
};

const switchPanel = (panel) => {
  if (panel === 'selection') {
    selectionPanel.hidden = false;
    timerPanel.hidden = true;
    app.dataset.screen = 'selection';
  } else {
    selectionPanel.hidden = true;
    timerPanel.hidden = false;
    app.dataset.screen = 'timer';
  }
};

const startTimer = (minutes, label, accent) => {
  cancelTimer();
  lastConfig = { minutes, label, accent };
  totalDuration = minutes * 60;
  finishTimestamp = performance.now() + totalDuration * 1000;
  timerActive = true;

  timerLabel.textContent = label;
  timerStatus.textContent = 'Countdown attivo';
  timerRing.classList.remove('completed');
  timerRing.style.setProperty('--progress', '0');
  setAccent(accent);

  switchPanel('timer');
  animationFrameId = requestAnimationFrame(updateProgress);
};

optionButtons.forEach((button) => {
  const accent = button.dataset.accent || DEFAULT_ACCENT;
  button.style.setProperty('--option-accent', accent);

  if (!button.querySelector('span')) {
    button.insertAdjacentHTML('beforeend', `<span>${button.dataset.label}</span>`);
  }

  button.addEventListener('click', () => {
    const minutes = Number(button.dataset.minutes);
    const label = button.dataset.label || `${minutes} minuti`;
    const accentColor = button.dataset.accent || DEFAULT_ACCENT;
    startTimer(minutes, label, accentColor);
  });
});

backButton.addEventListener('click', () => {
  cancelTimer();
  setAccent(DEFAULT_ACCENT);
  switchPanel('selection');
});

resetButton.addEventListener('click', () => {
  if (!lastConfig) return;
  startTimer(lastConfig.minutes, lastConfig.label, lastConfig.accent);
});

window.addEventListener('visibilitychange', () => {
  if (!timerActive || document.visibilityState !== 'visible') {
    return;
  }
  animationFrameId = requestAnimationFrame(updateProgress);
});
