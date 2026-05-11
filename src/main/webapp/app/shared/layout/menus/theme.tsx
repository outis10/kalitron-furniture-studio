import React from 'react';
import { Dropdown, DropdownMenu, DropdownToggle, Nav } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { ThemeType } from 'app/shared/reducers/theme';

export interface IThemeMenuProps {
  currentTheme: ThemeType;
  onClick: (theme: ThemeType) => void;
}

export const themes: Record<ThemeType, { name: string }> = {
  default: { name: 'Default' },
  cerulean: { name: 'Cerulean' },
  cosmo: { name: 'Cosmo' },
  cyborg: { name: 'Cyborg' },
  darkly: { name: 'Darkly' },
  flatly: { name: 'Flatly' },
  journal: { name: 'Journal' },
  litera: { name: 'Litera' },
  lumen: { name: 'Lumen' },
  lux: { name: 'Lux' },
  materia: { name: 'Materia' },
  minty: { name: 'Minty' },
  morph: { name: 'Morph' },
  pulse: { name: 'Pulse' },
  quartz: { name: 'Quartz' },
  sandstone: { name: 'Sandstone' },
  simplex: { name: 'Simplex' },
  sketchy: { name: 'Sketchy' },
  slate: { name: 'Slate' },
  solar: { name: 'Solar' },
  spacelab: { name: 'Spacelab' },
  superhero: { name: 'Superhero' },
  united: { name: 'United' },
  vapor: { name: 'Vapor' },
  yeti: { name: 'Yeti' },
  zephyr: { name: 'Zephyr' },
};

export const ThemeMenu = ({ currentTheme, onClick }: IThemeMenuProps) => (
  <Dropdown as={Nav.Item} id="theme-menu">
    <DropdownToggle as={Nav.Link} className="d-flex align-items-center">
      <FontAwesomeIcon icon="paint-brush" />
      <span>{themes[currentTheme]?.name ?? 'Theme'}</span>
    </DropdownToggle>
    <DropdownMenu renderOnMount align="end" style={{ maxHeight: '70vh', overflowY: 'auto' }}>
      {(Object.keys(themes) as ThemeType[]).map(theme => (
        <Dropdown.Item key={theme} active={theme === currentTheme} onClick={() => onClick(theme)}>
          {themes[theme].name}
        </Dropdown.Item>
      ))}
    </DropdownMenu>
  </Dropdown>
);
