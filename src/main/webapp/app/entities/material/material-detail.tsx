import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './material.reducer';

export const MaterialDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const materialEntity = useAppSelector(state => state.material.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="materialDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.material.detail.title">Material</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{materialEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="kalitronFurnitureStudioApp.material.code">Code</Translate>
            </span>
          </dt>
          <dd>{materialEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="kalitronFurnitureStudioApp.material.name">Name</Translate>
            </span>
          </dt>
          <dd>{materialEntity.name}</dd>
          <dt>
            <span id="materialKind">
              <Translate contentKey="kalitronFurnitureStudioApp.material.materialKind">Material Kind</Translate>
            </span>
          </dt>
          <dd>{materialEntity.materialKind}</dd>
          <dt>
            <span id="thicknessMm">
              <Translate contentKey="kalitronFurnitureStudioApp.material.thicknessMm">Thickness Mm</Translate>
            </span>
          </dt>
          <dd>{materialEntity.thicknessMm}</dd>
          <dt>
            <span id="sheetWidthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.material.sheetWidthMm">Sheet Width Mm</Translate>
            </span>
          </dt>
          <dd>{materialEntity.sheetWidthMm}</dd>
          <dt>
            <span id="sheetHeightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.material.sheetHeightMm">Sheet Height Mm</Translate>
            </span>
          </dt>
          <dd>{materialEntity.sheetHeightMm}</dd>
          <dt>
            <span id="costPerSheetMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.material.costPerSheetMxn">Cost Per Sheet Mxn</Translate>
            </span>
          </dt>
          <dd>{materialEntity.costPerSheetMxn}</dd>
          <dt>
            <span id="costPerSquareMeterMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.material.costPerSquareMeterMxn">Cost Per Square Meter Mxn</Translate>
            </span>
          </dt>
          <dd>{materialEntity.costPerSquareMeterMxn}</dd>
          <dt>
            <span id="supplierName">
              <Translate contentKey="kalitronFurnitureStudioApp.material.supplierName">Supplier Name</Translate>
            </span>
          </dt>
          <dd>{materialEntity.supplierName}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="kalitronFurnitureStudioApp.material.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{materialEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.material.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{materialEntity.notes}</dd>
        </dl>
        <Button as={Link as any} to="/material" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/material/${materialEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MaterialDetail;
