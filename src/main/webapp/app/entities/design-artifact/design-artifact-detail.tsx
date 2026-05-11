import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './design-artifact.reducer';

export const DesignArtifactDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const designArtifactEntity = useAppSelector(state => state.designArtifact.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="designArtifactDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.detail.title">DesignArtifact</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.id}</dd>
          <dt>
            <span id="artifactType">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.artifactType">Artifact Type</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.artifactType}</dd>
          <dt>
            <span id="fileName">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.fileName">File Name</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.fileName}</dd>
          <dt>
            <span id="filePath">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.filePath">File Path</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.filePath}</dd>
          <dt>
            <span id="mimeType">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.mimeType">Mime Type</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.mimeType}</dd>
          <dt>
            <span id="fileSizeKb">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.fileSizeKb">File Size Kb</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.fileSizeKb}</dd>
          <dt>
            <span id="checksum">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.checksum">Checksum</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.checksum}</dd>
          <dt>
            <span id="metadataJson">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.metadataJson">Metadata Json</Translate>
            </span>
          </dt>
          <dd>{designArtifactEntity.metadataJson}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {designArtifactEntity.createdAt ? (
              <TextFormat value={designArtifactEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.session">Session</Translate>
          </dt>
          <dd>{designArtifactEntity.session ? designArtifactEntity.session.sessionCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/design-artifact" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/design-artifact/${designArtifactEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DesignArtifactDetail;
