import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './design-image.reducer';

export const DesignImageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const designImageEntity = useAppSelector(state => state.designImage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="designImageDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.designImage.detail.title">DesignImage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.id}</dd>
          <dt>
            <span id="imageType">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.imageType">Image Type</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.imageType}</dd>
          <dt>
            <span id="fileName">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.fileName">File Name</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.fileName}</dd>
          <dt>
            <span id="filePath">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.filePath">File Path</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.filePath}</dd>
          <dt>
            <span id="mimeType">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.mimeType">Mime Type</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.mimeType}</dd>
          <dt>
            <span id="fileSizeKb">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.fileSizeKb">File Size Kb</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.fileSizeKb}</dd>
          <dt>
            <span id="widthPx">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.widthPx">Width Px</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.widthPx}</dd>
          <dt>
            <span id="heightPx">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.heightPx">Height Px</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.heightPx}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="uploadedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.uploadedAt">Uploaded At</Translate>
            </span>
          </dt>
          <dd>
            {designImageEntity.uploadedAt ? <TextFormat value={designImageEntity.uploadedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="description">
              <Translate contentKey="kalitronFurnitureStudioApp.designImage.description">Description</Translate>
            </span>
          </dt>
          <dd>{designImageEntity.description}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.designImage.session">Session</Translate>
          </dt>
          <dd>{designImageEntity.session ? designImageEntity.session.sessionCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/design-image" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/design-image/${designImageEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DesignImageDetail;
