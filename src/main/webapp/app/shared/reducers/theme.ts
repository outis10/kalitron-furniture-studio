import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Storage } from 'react-jhipster';

export type ThemeType =
  | 'default'
  | 'cerulean'
  | 'cosmo'
  | 'cyborg'
  | 'darkly'
  | 'flatly'
  | 'journal'
  | 'litera'
  | 'lumen'
  | 'lux'
  | 'materia'
  | 'minty'
  | 'morph'
  | 'pulse'
  | 'quartz'
  | 'sandstone'
  | 'simplex'
  | 'sketchy'
  | 'slate'
  | 'solar'
  | 'spacelab'
  | 'superhero'
  | 'united'
  | 'vapor'
  | 'yeti'
  | 'zephyr';

export interface ThemeState {
  currentTheme: ThemeType;
}

const storedTheme = Storage.local.get('theme') as ThemeType;
const initialTheme: ThemeType = storedTheme || 'default';

const initialState: ThemeState = {
  currentTheme: initialTheme,
};

const navbarColors: Record<ThemeType, { bg: string; text: string }> = {
  default: { bg: '#353d47', text: '#fff' },
  cerulean: { bg: '#007bff', text: '#fff' },
  cosmo: { bg: '#2c3e50', text: '#fff' },
  cyborg: { bg: '#222222', text: '#fff' },
  darkly: { bg: '#222222', text: '#fff' },
  flatly: { bg: '#2c3e50', text: '#fff' },
  journal: { bg: '#f8f9f9', text: '#333' },
  litera: { bg: '#f8f9f9', text: '#333' },
  lumen: { bg: '#f8f9f9', text: '#333' },
  lux: { bg: '#fff', text: '#333' },
  materia: { bg: '#e74c3c', text: '#fff' },
  minty: { bg: '#01847f', text: '#fff' },
  morph: { bg: '#1e1e2e', text: '#fff' },
  pulse: { bg: '#f0f0f0', text: '#333' },
  quartz: { bg: '#422d49', text: '#fff' },
  sandstone: { bg: '#d0ccc8', text: '#333' },
  simplex: { bg: '#fff', text: '#333' },
  sketchy: { bg: '#fff', text: '#333' },
  slate: { bg: '#3e4348', text: '#fff' },
  solar: { bg: '#002b36', text: '#fff' },
  spacelab: { bg: '#003366', text: '#fff' },
  superhero: { bg: '#1f1f1f', text: '#fff' },
  united: { bg: '#e74c3c', text: '#fff' },
  vapor: { bg: '#494d5c', text: '#fff' },
  yeti: { bg: '#222', text: '#fff' },
  zephyr: { bg: '#f3f3f1', text: '#333' },
};

const loadTheme = (theme: ThemeType) => {
  if (typeof document !== 'undefined') {
    let themeLink = document.getElementById('theme-link') as HTMLLinkElement;

    if (theme === 'default') {
      if (themeLink) {
        themeLink.remove();
      }
    } else {
      if (!themeLink) {
        themeLink = document.createElement('link');
        themeLink.id = 'theme-link';
        themeLink.rel = 'stylesheet';
        document.head.appendChild(themeLink);
      }
      themeLink.href = `https://cdn.jsdelivr.net/npm/bootswatch@5.3.0/dist/${theme}/bootstrap.min.css`;
    }

    const colors = navbarColors[theme];
    let styleElement = document.getElementById('theme-navbar-style') as HTMLStyleElement;

    if (!styleElement) {
      styleElement = document.createElement('style');
      styleElement.id = 'theme-navbar-style';
      document.head.appendChild(styleElement);
    }

    styleElement.textContent = `
      .jh-navbar {
        background-color: ${colors.bg} !important;
      }
      .jh-navbar a,
      .jh-navbar .nav-link,
      .jh-navbar .navbar-brand,
      .jh-navbar .dropdown-toggle {
        color: ${colors.text} !important;
      }
      .jh-navbar .dropdown-item {
        color: #333 !important;
      }
      .jh-navbar .dropdown-item.active,
      .jh-navbar .dropdown-item.active:focus,
      .jh-navbar .dropdown-item.active:hover {
        background-color: #e9ecef !important;
        color: #333 !important;
      }
      .jh-navbar .dropdown-item:hover {
        background-color: #f8f9f9 !important;
      }
      .jh-navbar .navbar-toggler {
        border-color: ${colors.text};
      }
      .jh-navbar .navbar-toggler-icon {
        background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3e%3cpath stroke='${encodeURIComponent(colors.text)}' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e");
      }
      .brand-title {
        color: ${colors.text} !important;
      }
      .brand-title:hover {
        color: ${colors.text} !important;
      }
    `;
  }
};

loadTheme(initialTheme);

export const ThemeSlice = createSlice({
  name: 'theme',
  initialState,
  reducers: {
    setTheme(state, action: PayloadAction<ThemeType>) {
      const theme = action.payload;
      state.currentTheme = theme;
      Storage.local.set('theme', theme);
      loadTheme(theme);
    },
  },
});

export const { setTheme } = ThemeSlice.actions;

export default ThemeSlice.reducer;
