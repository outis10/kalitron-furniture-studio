import React, { useState } from 'react';
import { DropdownItem, Dropdown, DropdownToggle, DropdownMenu } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { ThemeType } from 'app/shared/reducers/theme';

export interface IThemeMenuProps {
  currentTheme: ThemeType;
  onClick: (event: any) => void;
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

export const ThemeMenu = ({ currentTheme, onClick }: IThemeMenuProps) => {
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const toggleDropdown = () => setDropdownOpen(!dropdownOpen);

  const handleThemeClick = (event: any) => {
    setDropdownOpen(false);
    Promise.resolve().then(() => {
      onClick(event);
    });
  };

  return (
    <Dropdown nav inNavbar isOpen={dropdownOpen} toggle={toggleDropdown} id="theme-menu">
      <DropdownToggle nav caret className="d-flex align-items-center">
        <FontAwesomeIcon icon="paint-brush" />
        <span>{currentTheme ? themes[currentTheme].name : 'Theme'}</span>
      </DropdownToggle>
      <DropdownMenu end role="menu">
        {(Object.keys(themes) as ThemeType[]).map(theme => (
          <DropdownItem key={theme} value={theme} onClick={handleThemeClick} active={theme === currentTheme} role="menuitem">
            {themes[theme].name}
          </DropdownItem>
        ))}
      </DropdownMenu>
    </Dropdown>
  );
};
