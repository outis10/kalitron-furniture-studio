import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './hardware.reducer';

export const HardwareDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const hardwareEntity = useAppSelector(state => state.hardware.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="hardwareDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.hardware.detail.title">Hardware</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.code">Code</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.name">Name</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.name}</dd>
          <dt>
            <span id="hardwareType">
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.hardwareType">Hardware Type</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.hardwareType}</dd>
          <dt>
            <span id="unitCostMxn">
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.unitCostMxn">Unit Cost Mxn</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.unitCostMxn}</dd>
          <dt>
            <span id="supplierName">
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.supplierName">Supplier Name</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.supplierName}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.hardware.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{hardwareEntity.notes}</dd>
        </dl>
        <Button as={Link as any} to="/hardware" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/hardware/${hardwareEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default HardwareDetail;
