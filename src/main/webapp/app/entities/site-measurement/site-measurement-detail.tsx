import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './site-measurement.reducer';

export const SiteMeasurementDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const siteMeasurementEntity = useAppSelector(state => state.siteMeasurement.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="siteMeasurementDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.detail.title">SiteMeasurement</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.id}</dd>
          <dt>
            <span id="measurementUuid">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.measurementUuid">Measurement Uuid</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.measurementUuid}</dd>
          <dt>
            <span id="projectType">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.projectType">Project Type</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.projectType}</dd>
          <dt>
            <span id="revision">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.revision">Revision</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.revision}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.status">Status</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.status}</dd>
          <dt>
            <span id="schemaVersion">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.schemaVersion">Schema Version</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.schemaVersion}</dd>
          <dt>
            <span id="catalogVersion">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.catalogVersion">Catalog Version</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.catalogVersion}</dd>
          <dt>
            <span id="payload">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.payload">Payload</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.payload}</dd>
          <dt>
            <span id="payloadSha256">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.payloadSha256">Payload Sha 256</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.payloadSha256}</dd>
          <dt>
            <span id="floorOutOfLevelMm">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.floorOutOfLevelMm">Floor Out Of Level Mm</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.floorOutOfLevelMm}</dd>
          <dt>
            <span id="floorOutOfLevelNote">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.floorOutOfLevelNote">Floor Out Of Level Note</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.floorOutOfLevelNote}</dd>
          <dt>
            <span id="deviceId">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.deviceId">Device Id</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.deviceId}</dd>
          <dt>
            <span id="appVersion">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.appVersion">App Version</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.appVersion}</dd>
          <dt>
            <span id="laserModel">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.laserModel">Laser Model</Translate>
            </span>
          </dt>
          <dd>{siteMeasurementEntity.laserModel}</dd>
          <dt>
            <span id="capturedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.capturedAt">Captured At</Translate>
            </span>
          </dt>
          <dd>
            {siteMeasurementEntity.capturedAt ? (
              <TextFormat value={siteMeasurementEntity.capturedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="receivedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.receivedAt">Received At</Translate>
            </span>
          </dt>
          <dd>
            {siteMeasurementEntity.receivedAt ? (
              <TextFormat value={siteMeasurementEntity.receivedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="confirmedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.confirmedAt">Confirmed At</Translate>
            </span>
          </dt>
          <dd>
            {siteMeasurementEntity.confirmedAt ? (
              <TextFormat value={siteMeasurementEntity.confirmedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.session">Session</Translate>
          </dt>
          <dd>{siteMeasurementEntity.session ? siteMeasurementEntity.session.sessionCode : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.measuredBy">Measured By</Translate>
          </dt>
          <dd>{siteMeasurementEntity.measuredBy ? siteMeasurementEntity.measuredBy.login : ''}</dd>
        </dl>
        <Button as={Link as any} to="/site-measurement" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/site-measurement/${siteMeasurementEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SiteMeasurementDetail;
