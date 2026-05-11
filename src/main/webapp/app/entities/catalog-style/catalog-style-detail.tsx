import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './catalog-style.reducer';

export const CatalogStyleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const catalogStyleEntity = useAppSelector(state => state.catalogStyle.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="catalogStyleDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.detail.title">CatalogStyle</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.name">Name</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.description">Description</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.description}</dd>
          <dt>
            <span id="thumbnailPath">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.thumbnailPath">Thumbnail Path</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.thumbnailPath}</dd>
          <dt>
            <span id="style">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.style">Style</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.style}</dd>
          <dt>
            <span id="primaryFinish">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.primaryFinish">Primary Finish</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.primaryFinish}</dd>
          <dt>
            <span id="priceRange">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.priceRange">Price Range</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.priceRange}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{catalogStyleEntity.sortOrder}</dd>
        </dl>
        <Button as={Link as any} to="/catalog-style" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/catalog-style/${catalogStyleEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CatalogStyleDetail;
