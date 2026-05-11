import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cabinet-template.reducer';

export const CabinetTemplateDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cabinetTemplateEntity = useAppSelector(state => state.cabinetTemplate.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cabinetTemplateDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.detail.title">CabinetTemplate</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.code">Code</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.name">Name</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.name}</dd>
          <dt>
            <span id="category">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.category">Category</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.category}</dd>
          <dt>
            <span id="defaultWidthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.defaultWidthMm">Default Width Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.defaultWidthMm}</dd>
          <dt>
            <span id="defaultHeightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.defaultHeightMm">Default Height Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.defaultHeightMm}</dd>
          <dt>
            <span id="defaultDepthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.defaultDepthMm">Default Depth Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.defaultDepthMm}</dd>
          <dt>
            <span id="minWidthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.minWidthMm">Min Width Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.minWidthMm}</dd>
          <dt>
            <span id="maxWidthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.maxWidthMm">Max Width Mm</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.maxWidthMm}</dd>
          <dt>
            <span id="supportsDoors">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.supportsDoors">Supports Doors</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.supportsDoors ? 'true' : 'false'}</dd>
          <dt>
            <span id="supportsDrawers">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.supportsDrawers">Supports Drawers</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.supportsDrawers ? 'true' : 'false'}</dd>
          <dt>
            <span id="supportsShelves">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.supportsShelves">Supports Shelves</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.supportsShelves ? 'true' : 'false'}</dd>
          <dt>
            <span id="fusionTemplateName">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.fusionTemplateName">Fusion Template Name</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.fusionTemplateName}</dd>
          <dt>
            <span id="csvProfileJson">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.csvProfileJson">Csv Profile Json</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.csvProfileJson}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{cabinetTemplateEntity.sortOrder}</dd>
        </dl>
        <Button as={Link as any} to="/cabinet-template" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/cabinet-template/${cabinetTemplateEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CabinetTemplateDetail;
