import './measurer-assignment.scss';

import React from 'react';
import { Alert, Form, Spinner } from 'react-bootstrap';
import { Link } from 'react-router';

import { measurerDisplayName } from 'app/shared/api/measurer-api';
import { useMeasurerAssignment } from 'app/shared/hooks/useMeasurerAssignment';

interface MeasurerAssignmentProps {
  sessionId: number;
}

/** "Medidor asignado" select for a session (E12 #116). Rendered only for admins. */
const MeasurerAssignment = ({ sessionId }: MeasurerAssignmentProps) => {
  const { measurers, assignedLogin, isLoading, isSaving, error, savedMessage, assign } = useMeasurerAssignment(sessionId);
  const selectId = `measurer-assignment-${sessionId}`;

  if (!isLoading && !error && measurers.length === 0 && !assignedLogin) {
    return (
      <div className="measurer-assignment">
        <p className="design-chat__meta mb-0">
          No hay usuarios con rol de medidor. <Link to="/admin/user-management">Administrar usuarios</Link>
        </p>
      </div>
    );
  }

  return (
    <div className="measurer-assignment">
      <Form.Label htmlFor={selectId} className="design-chat__meta mb-1">
        Medidor asignado {isLoading || isSaving ? <Spinner size="sm" aria-label="Cargando" /> : null}
      </Form.Label>
      <Form.Select
        id={selectId}
        value={assignedLogin ?? ''}
        disabled={isLoading || isSaving}
        onChange={event => assign(event.target.value || null)}
      >
        <option value="">Sin asignar</option>
        {measurers.map(measurer => (
          <option key={measurer.login} value={measurer.login}>
            {measurerDisplayName(measurer)}
          </option>
        ))}
      </Form.Select>
      {error ? (
        <Alert variant="danger" className="mt-2 mb-0 py-1 px-2">
          {error}
        </Alert>
      ) : null}
      {savedMessage && !error ? (
        <p className="text-success small mt-1 mb-0" role="status">
          {savedMessage}
        </p>
      ) : null}
    </div>
  );
};

export default MeasurerAssignment;
