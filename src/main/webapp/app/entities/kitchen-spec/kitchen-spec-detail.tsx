import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './kitchen-spec.reducer';

export const KitchenSpecDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const kitchenSpecEntity = useAppSelector(state => state.kitchenSpec.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="kitchenSpecDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.detail.title">KitchenSpec</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.id}</dd>
          <dt>
            <span id="layout">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.layout">Layout</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.layout}</dd>
          <dt>
            <span id="totalWidthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.totalWidthMm">Total Width Mm</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.totalWidthMm}</dd>
          <dt>
            <span id="totalHeightMm">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.totalHeightMm">Total Height Mm</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.totalHeightMm}</dd>
          <dt>
            <span id="totalDepthMm">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.totalDepthMm">Total Depth Mm</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.totalDepthMm}</dd>
          <dt>
            <span id="style">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.style">Style</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.style}</dd>
          <dt>
            <span id="primaryFinish">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.primaryFinish">Primary Finish</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.primaryFinish}</dd>
          <dt>
            <span id="handleType">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.handleType">Handle Type</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.handleType}</dd>
          <dt>
            <span id="countertopMaterial">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.countertopMaterial">Countertop Material</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.countertopMaterial}</dd>
          <dt>
            <span id="sinkPosition">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.sinkPosition">Sink Position</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.sinkPosition}</dd>
          <dt>
            <span id="confirmedByClient">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.confirmedByClient">Confirmed By Client</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.confirmedByClient ? 'true' : 'false'}</dd>
          <dt>
            <span id="extractedJson">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.extractedJson">Extracted Json</Translate>
            </span>
          </dt>
          <dd>{kitchenSpecEntity.extractedJson}</dd>
          <dt>
            <span id="confirmedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.confirmedAt">Confirmed At</Translate>
            </span>
          </dt>
          <dd>
            {kitchenSpecEntity.confirmedAt ? (
              <TextFormat value={kitchenSpecEntity.confirmedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.primaryMaterial">Primary Material</Translate>
          </dt>
          <dd>{kitchenSpecEntity.primaryMaterial ? kitchenSpecEntity.primaryMaterial.name : ''}</dd>
        </dl>
        <Button as={Link as any} to="/kitchen-spec" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/kitchen-spec/${kitchenSpecEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default KitchenSpecDetail;
