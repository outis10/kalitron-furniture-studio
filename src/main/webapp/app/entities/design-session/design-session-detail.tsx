import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './design-session.reducer';

export const DesignSessionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const designSessionEntity = useAppSelector(state => state.designSession.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="designSessionDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.designSession.detail.title">DesignSession</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.id}</dd>
          <dt>
            <span id="sessionCode">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.sessionCode">Session Code</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.sessionCode}</dd>
          <dt>
            <span id="projectType">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.projectType">Project Type</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.projectType}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.status">Status</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.status}</dd>
          <dt>
            <span id="clientName">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.clientName">Client Name</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.clientName}</dd>
          <dt>
            <span id="clientEmail">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.clientEmail">Client Email</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.clientEmail}</dd>
          <dt>
            <span id="clientPhone">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.clientPhone">Client Phone</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.clientPhone}</dd>
          <dt>
            <span id="selectedStyle">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.selectedStyle">Selected Style</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.selectedStyle}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{designSessionEntity.notes}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {designSessionEntity.createdAt ? (
              <TextFormat value={designSessionEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.designSession.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {designSessionEntity.updatedAt ? (
              <TextFormat value={designSessionEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.designSession.spec">Spec</Translate>
          </dt>
          <dd>{designSessionEntity.spec ? designSessionEntity.spec.style : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.designSession.catalogStyle">Catalog Style</Translate>
          </dt>
          <dd>{designSessionEntity.catalogStyle ? designSessionEntity.catalogStyle.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/design-session" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/design-session/${designSessionEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DesignSessionDetail;
