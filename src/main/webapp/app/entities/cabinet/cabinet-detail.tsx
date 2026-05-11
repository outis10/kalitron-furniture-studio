import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cabinet.reducer';

export const CabinetDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cabinetEntity = useAppSelector(state => state.cabinet.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cabinetDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.cabinet.detail.title">Cabinet</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.id}</dd>
          <dt>
            <span id="cabinetCode">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.cabinetCode">Cabinet Code</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.cabinetCode}</dd>
          <dt>
            <span id="category">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.category">Category</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.category}</dd>
          <dt>
            <span id="label">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.label">Label</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.label}</dd>
          <dt>
            <span id="widthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.widthMm">Width Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.widthMm}</dd>
          <dt>
            <span id="heightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.heightMm">Height Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.heightMm}</dd>
          <dt>
            <span id="depthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.depthMm">Depth Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.depthMm}</dd>
          <dt>
            <span id="doors">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.doors">Doors</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.doors}</dd>
          <dt>
            <span id="drawers">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.drawers">Drawers</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.drawers}</dd>
          <dt>
            <span id="shelves">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.shelves">Shelves</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.shelves}</dd>
          <dt>
            <span id="finish">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.finish">Finish</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.finish}</dd>
          <dt>
            <span id="positionX">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionX">Position X</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.positionX}</dd>
          <dt>
            <span id="positionY">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionY">Position Y</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.positionY}</dd>
          <dt>
            <span id="positionZ">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionZ">Position Z</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.positionZ}</dd>
          <dt>
            <span id="rotationDeg">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.rotationDeg">Rotation Deg</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.rotationDeg}</dd>
          <dt>
            <span id="positionSeq">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.positionSeq">Position Seq</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.positionSeq}</dd>
          <dt>
            <span id="csvRowJson">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.csvRowJson">Csv Row Json</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.csvRowJson}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinet.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{cabinetEntity.notes}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.cabinet.template">Template</Translate>
          </dt>
          <dd>{cabinetEntity.template ? cabinetEntity.template.code : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.cabinet.material">Material</Translate>
          </dt>
          <dd>{cabinetEntity.material ? cabinetEntity.material.name : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.cabinet.spec">Spec</Translate>
          </dt>
          <dd>{cabinetEntity.spec ? cabinetEntity.spec.style : ''}</dd>
        </dl>
        <Button as={Link as any} to="/cabinet" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/cabinet/${cabinetEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CabinetDetail;
