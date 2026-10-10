import React from 'react';
import { MemoryRouter } from 'react-router';
import axios from 'axios';
import sinon from 'sinon';

import { fireEvent, render, screen, waitFor } from '@testing-library/react';

import MeasurerAssignment from './measurer-assignment';

const MEASURERS = [
  { login: 'ana', firstName: 'Ana', lastName: 'López' },
  { login: 'beto', firstName: 'Beto', lastName: null },
];

const stubGet = (measurers: unknown[], assignedLogin: string | null) =>
  sinon.stub().callsFake((url: string) =>
    Promise.resolve({
      data: url === 'api/measurers' ? measurers : { id: 12, assignedMeasurer: assignedLogin ? { login: assignedLogin } : null },
    }),
  );

const renderComponent = () =>
  render(
    <MemoryRouter>
      <MeasurerAssignment sessionId={12} />
    </MemoryRouter>,
  );

describe('MeasurerAssignment', () => {
  afterEach(() => sinon.restore());

  it('loads measurers and shows the current assignment', async () => {
    axios.get = stubGet(MEASURERS, 'ana');
    renderComponent();

    const select = await screen.findByLabelText(/Medidor asignado/);
    await waitFor(() => expect(select.disabled).toBe(false));
    expect(select.value).toBe('ana');
    expect(screen.getByRole('option', { name: 'Ana López' })).toBeTruthy();
    expect(screen.getByRole('option', { name: 'Beto' })).toBeTruthy();
    expect(screen.getByRole('option', { name: 'Sin asignar' })).toBeTruthy();
  });

  it('shows the empty state when there are no measurers', async () => {
    axios.get = stubGet([], null);
    renderComponent();

    expect(await screen.findByText(/No hay usuarios con rol de medidor/)).toBeTruthy();
  });

  it('shows an error when loading fails', async () => {
    axios.get = sinon.stub().returns(Promise.reject(new Error('boom')));
    renderComponent();

    expect(await screen.findByText('No se pudo cargar el medidor asignado.')).toBeTruthy();
  });

  it('assigns a measurer and confirms', async () => {
    axios.get = stubGet(MEASURERS, null);
    const put = sinon.stub().returns(Promise.resolve({ data: { sessionId: 12, assignedMeasurer: MEASURERS[1] } }));
    axios.put = put;
    renderComponent();

    const select = await screen.findByLabelText(/Medidor asignado/);
    await waitFor(() => expect(select.disabled).toBe(false));
    fireEvent.change(select, { target: { value: 'beto' } });

    expect(await screen.findByText('Medidor asignado.')).toBeTruthy();
    expect(put.calledWith('api/design-sessions/12/assigned-measurer', { userLogin: 'beto' })).toBe(true);
    expect(select.value).toBe('beto');
  });

  it('keeps the previous value when saving fails', async () => {
    axios.get = stubGet(MEASURERS, 'ana');
    axios.put = sinon.stub().returns(Promise.reject(new Error('400')));
    renderComponent();

    const select = await screen.findByLabelText(/Medidor asignado/);
    await waitFor(() => expect(select.disabled).toBe(false));
    fireEvent.change(select, { target: { value: 'beto' } });

    expect(await screen.findByText(/No se pudo guardar el medidor asignado/)).toBeTruthy();
    expect(select.value).toBe('ana');
  });
});
