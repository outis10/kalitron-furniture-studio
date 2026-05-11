import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cabinet-part.reducer';

export const CabinetPartDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cabinetPartEntity = useAppSelector(state => state.cabinetPart.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cabinetPartDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.detail.title">CabinetPart</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.id}</dd>
          <dt>
            <span id="partCode">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.partCode">Part Code</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.partCode}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.name">Name</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.name}</dd>
          <dt>
            <span id="widthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.widthMm">Width Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.widthMm}</dd>
          <dt>
            <span id="heightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.heightMm">Height Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.heightMm}</dd>
          <dt>
            <span id="thicknessMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.thicknessMm">Thickness Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.thicknessMm}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.quantity}</dd>
          <dt>
            <span id="edgeBanding">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.edgeBanding">Edge Banding</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.edgeBanding}</dd>
          <dt>
            <span id="grainDirection">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.grainDirection">Grain Direction</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.grainDirection}</dd>
          <dt>
            <span id="cncOperation">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.cncOperation">Cnc Operation</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.cncOperation}</dd>
          <dt>
            <span id="notes">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.notes">Notes</Translate>
            </span>
          </dt>
          <dd>{cabinetPartEntity.notes}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.material">Material</Translate>
          </dt>
          <dd>{cabinetPartEntity.material ? cabinetPartEntity.material.name : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetPart.cabinet">Cabinet</Translate>
          </dt>
          <dd>{cabinetPartEntity.cabinet ? cabinetPartEntity.cabinet.cabinetCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/cabinet-part" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/cabinet-part/${cabinetPartEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CabinetPartDetail;
